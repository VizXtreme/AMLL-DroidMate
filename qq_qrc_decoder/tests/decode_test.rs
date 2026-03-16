use amll_lyric::eqrc::qdec::TripleQDES;
use flate2::read::ZlibDecoder;
use std::io::Read;

/// This test is used to inspect the raw decrypted bytes for the QQ lyric_download.fcg payload.
/// The payload in question does not currently decompress via standard zlib, so we inspect it.
#[test]
fn dump_decrypted_payload_bytes() {
    let hex_data = "00367FE8E50542ABECE8E677924C7C2DF977E3910E7E4272C1D871D80BFF1C12E71AEC96FF14066D18DB3C9A36181E3489C695667311BDC4B3AE764754CD0C3EF8E7939D7BC630D19C470098B6E380BBF365F6DEBDEC438477A0A67B986E76A5863F1FE9E0C064936BAB9D5B7F48B4D5E732478C94453991BFA073F7D906F8837D998F0F1CF8529C8116FE1631683E7F28696BBCAA1DBB09FC50CA505B39626EBC0B25E29281047A51906BE626B6AE79D507E125D738AEA5A212CB18304EC6D9D1E26380595D3FD18F5E0C025FC87F5B334E2B466DA584B930C2557616970378FD399AB271360DA939494536327156112AA383BC3E06237FDDFB1EC48916B85C9B4C0AD0745CFDA641BE78EE4AF707CFAD9AAB21511CF508EF0861E1D9D7F6F1B925974ABE0D287B3B40CB3F2C624A2EFFCC6F4AB61E3E91E8DB0A4CC986F60D75D24A99245920347B992DA15FBD069814E266DA6C6A87D67CCA1585D4F403282A6812703A1AF3E54617369E1F10D4E850850B21445A066BAEDE07982E4995AE662B40D71461388A92DBD0E1A9CDCCB8EA425BED5705F343C3A84B5844C2B31CBAAF85C5CADD1E65D73A402664F260DA788297ED67D049463C39FDF19FFFA00A024D512BD9AA69C7E5E996A2578EAFE9D7C2F223FDBAF7C144AAF4170C6802A40122A0AA592662F7FF0A3F53EF0B316BF743D6D40FB6162DA0C3F725D88B2835E458CEA9F30C63D32B65CE4DE12FAED0B8FE345C1AB3B69EC507BFA87E322F434E12A97B33B4BA95219112D9299D90E6121BEDBC8278943F2035F25F9DE6C174CAC8C3460C25E5DB4B1226A4BE07B366E98C26D756D56DC94EAA7639616A5441417BD0199059D4A71118CDFDCE41069B3A7F51D839A015B6ACDD2BCAD5E07AB4E8E2770D74C7CC91C873697F91660D9BF8F86F319C10A45623C1EDA880D56F24FB76BD111F7F3970EC1598DF2BF6A54914AC1795F59AC958006E810CC5EA8A41C55E0F39A0DA57690C5FFDF301836D099166C34EAC3F021AE998FB29F48B3C9855CC1C4529C5FF767C83EBF5B0DCAE86BC2AC7EEBDEED7BA54AB88221311F80D6105F9446B5B951F3946572C72BA4BA2";

    // Perform the same 3DES decryption used by the lyric decoder.
    let mut bytes = hex::decode(hex_data).expect("invalid hex");
    let key = b"!@#)(*$%123ZXC!@!@#)(NHL";
    let cipher = TripleQDES::new(key, true);

    for chunk in bytes.chunks_exact_mut(8) {
        let mut block: [u8; 8] = chunk.try_into().unwrap();
        cipher.crypt_inplace(&mut block);
        chunk.copy_from_slice(&block);
    }

    // Print out the first 64 bytes of the decrypted output for inspection.
    println!("decrypted first 64 bytes: {:02X?}", &bytes[..64]);

    // Try to locate a valid zlib header after simple XOR transformations.
    // If we find one, attempt decompression and print the first few bytes.
    for &xor_key in &[0u8, 0xFFu8, 0xAAu8, 0x55u8] {
        let xored: Vec<u8> = bytes.iter().map(|b| b ^ xor_key).collect();
        for offset in 0..xored.len().saturating_sub(1) {
            if xored[offset] == 0x78 {
                let cmf = 0x78u8;
                let flg = xored[offset + 1];
                let combined = (u16::from(cmf) << 8) | u16::from(flg);
                if combined % 31 == 0 {
                    let mut decoder = ZlibDecoder::new(&xored[offset..]);
                    let mut out = Vec::new();
                    if decoder.read_to_end(&mut out).is_ok() {
                        println!("found zlib at offset {} with xor_key=0x{:02X}, decompressed {} bytes", offset, xor_key, out.len());
                        println!("preview: {}", String::from_utf8_lossy(&out[..out.len().min(200)]));
                        return;
                    }
                }
            }
        }
    }

    panic!("No workable zlib stream found in decrypted output");
}
