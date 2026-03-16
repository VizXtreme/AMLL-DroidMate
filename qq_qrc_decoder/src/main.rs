use std::env;

fn hex_to_bytes(hex: &str) -> Option<Vec<u8>> {
    let hex = hex.trim();
    if hex.len() % 2 != 0 {
        return None;
    }
    (0..hex.len())
        .step_by(2)
        .map(|i| u8::from_str_radix(&hex[i..i + 2], 16).ok())
        .collect()
}

fn print_hex_preview(bytes: &[u8], len: usize) {
    let preview = &bytes[..bytes.len().min(len)];
    let s: String = preview.iter().map(|b| format!("{:02X}", b)).collect::<Vec<_>>().join(" ");
    println!("{} bytes: {}", bytes.len(), s);
}

fn try_decompress_at_offset(bytes: &[u8], offset: usize) {
    use flate2::read::ZlibDecoder;
    use std::io::Read;

    if offset >= bytes.len() {
        return;
    }

    println!("\n-- Trying decompression at offset {} --", offset);
    let slice = &bytes[offset..];
    let mut decoder = ZlibDecoder::new(slice);
    let mut decompressed = Vec::new();
    match decoder.read_to_end(&mut decompressed) {
        Ok(_) => {
            println!("Decompressed {} bytes", decompressed.len());
            println!("Preview: {}", String::from_utf8_lossy(&decompressed[..decompressed.len().min(200)]));
        }
        Err(e) => {
            println!("Decompression failed: {}", e);
        }
    }
}

const DEBUG_HEX: &str = "5B7E01E01CA39AC570D81F7282D678A13EFA969B88F913C7E9DDBF54871911F1C86D2AADF82D093E6069C7D3E3722349345D8CC272099118C33C7A6AEC60BE31133AD7498C5C762241D52093BB7EB6763B7B3B0245D4204DFAE282ADAE4A5C23860822C9EC543DBA3E6920116A00540A2D9763915B3D463612E10A691F01EC882B2E0A508E3E1DDAFBA80A1C0715CD7C64EAA69CAE23BBE2A277767DEEDA04A6A3B4EC2CF4F8394807BE277C4465C6A73676E2703D107CA69EB1B6B0D30E26540BB99EA30502A5C3A1DE811E723A40C9ACA616011CB059036082509ABB49B2B932D00273498B168CE84DA16AECB01919171B04FF9025C4EA54B38565C022024D5F2FF1AB299E33F60F2CF1E9E9702A6832170798C998E7AD8B2EF82046CFA2965FE97A74D7BB9950EBD7C94341ADB8BC67CE2D00E1A1D270AB7F979D33DAD058D4ED88ABBCBEADCF24AD7526B159EA2FD7C8A7F05877C603651738FFCB0B9317FA7D6A16B45EFF298753CE797BD58F823A31F207D7E597A9841C0957FFA0DAB9996D4CCF792A0B73F64D7F64F863F50C0B7078A829C9B79F6D3DB52EDF04D9C8BC2BF65E533471CC4FBBA98C622998176C0BBDA1C5EDE2701F7B172DDB1899CE84BC52080AE9C128D3A36E7F4BB98447DB156786302B6AD42957D7123D1DA5278A608F29B29F0DAA959DFE3F7ED346B202BF67F6E0EDE8B0DA9B437E9870B0F58DAD832A63C40DA614A6D5E698282AC6F94318798A7F65DC38EBAFC447357F02002D5B30A304344125E4E5EF43D267E17A838BF43345F3D9906579C5F4B36A627E5C9C23DB195C5268322A62B3A49568C7777A5DC79AC38F35DC1327A48ED0EC5EED15D0F30AB5032397BD0FC46A3082C8009C5B51B86FAE52BAA058A4257F04AF514BB56A1C4B5D8F821DBB46A68BABE4823309CD6F16F0B85D2D3F45282D743E581DF855B0B4CCF59EF445A06276B11B8B59CE4FFDB3457A08D688CDFA0E4216ABF0A449B8F76A76DF0222CBE422A6C209429D61DF4F3D766DB054DB2F49390B0C6F62028A6223E04445CF4DCC477DDF0647124107C121DB6BAF4ECA50C8163F3BCBD59CABBFD60780A5A4DEE0D5B0";

fn main() {
    let arg = env::args().nth(1);
    let hex = arg.unwrap_or_else(|| DEBUG_HEX.to_string());

    let encrypted = match hex_to_bytes(&hex) {
        Some(b) => b,
        None => {
            eprintln!("Invalid hex string");
            std::process::exit(1);
        }
    };

    println!("Encrypted bytes: {}", encrypted.len());
    print_hex_preview(&encrypted, 64);

    let decrypted = match qq_qrc_decoder::decrypt_qrc_hex_to_string(&hex) {
        Ok(s) => s,
        Err(e) => {
            println!("Decrypt failed: {}", e);
            std::process::exit(2);
        }
    };

    println!("\nDecrypted output length: {}", decrypted.len());
    println!("Decrypted preview: {}", decrypted.chars().take(200).collect::<String>());

    // Also attempt to locate zlib headers in the raw decrypted bytes.
    let raw = decrypted.into_bytes();
    let mut candidates = vec![];
    for i in 0..raw.len().saturating_sub(1) {
        if raw[i] == 0x78 && (raw[i + 1] == 0x9C || raw[i + 1] == 0x01 || raw[i + 1] == 0x5E) {
            candidates.push(i);
        }
    }

    println!("Found {} zlib-like header candidates", candidates.len());
    for &offset in candidates.iter().take(10) {
        try_decompress_at_offset(&raw, offset);
    }
}
