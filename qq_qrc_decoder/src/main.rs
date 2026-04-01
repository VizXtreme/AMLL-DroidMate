// ============================================================================
// QQ QRC 歌词文件解码器 - 主程序
// ============================================================================
// 
// 这个程序用于解密和解压 QQ 音乐的 .qrc 歌词文件。
// .qrc 是 QQ 音乐使用的加密歌词格式，基于标准 LRC 格式但进行了加密压缩。
// 
// **主要功能**：
// 1. 将十六进制字符串转换为字节数组
// 2. 调用 qq_qrc_decoder 库进行解密
// 3. 在解密后的数据中查找 zlib 压缩头
// 4. 尝试在不同偏移位置解压缩数据
// 5. 输出解密和解压结果（还原为标准 LRC 歌词）
// ============================================================================

use std::env;

// ============================================================================
// 辅助函数
// ============================================================================

/**
 * 将十六进制字符串转换为字节数组
 * 
 * @param hex 十六进制字符串（如 "5B7E01E0..."）
 * @return Some(Vec<u8>) 如果转换成功，否则 None
 * 
 * **处理流程**：
 * 1. 去除首尾空白字符
 * 2. 检查长度是否为偶数（每个字节需要 2 个十六进制字符）
 * 3. 每 2 个字符解析为一个 u8 字节
 * 
 * **应用场景**：
 * 从 QQ 音乐提取的 .qrc 文件通常以十六进制文本形式存储，
 * 需要先转换为二进制数据才能进行解密。
 */
fn hex_to_bytes(hex: &str) -> Option<Vec<u8>> {
    // 去除首尾空白字符
    let hex = hex.trim();
    
    // 长度必须为偶数（2 个字符 = 1 个字节）
    if hex.len() % 2 != 0 {
        return None;
    }
    
    // 每 2 个字符解析为一个字节
    (0..hex.len())
        .step_by(2)  // 步长为 2
        .map(|i| u8::from_str_radix(&hex[i..i + 2], 16).ok())  // 16 进制解析
        .collect()  // 收集为 Vec<u8>
}

/**
 * 打印十六进制预览
 * 
 * 输出字节数组的前 N 个字节的十六进制表示，便于调试。
 * 
 * @param bytes 字节数组
 * @param len 要显示的字节数
 */
fn print_hex_preview(bytes: &[u8], len: usize) {
    // 截取前 len 个字节（如果不足则取全部）
    let preview = &bytes[..bytes.len().min(len)];
    
    // 格式化为十六进制字符串（如 "5B 7E 01 E0 ..."）
    let s: String = preview.iter()
        .map(|b| format!("{:02X}", b))  // 大写十六进制，补齐 2 位
        .collect::<Vec<_>>()
        .join(" ");  // 用空格分隔
    
    println!("{} bytes: {}", bytes.len(), s);
}

/**
 * 尝试在指定偏移位置解压缩 zlib 数据
 * 
 * .qrc 文件解密后的数据包含 zlib 压缩的 LRC 歌词内容。
 * 由于加密算法的特性，可能需要在不同的偏移位置查找 zlib 头。
 * 
 * @param bytes 解密后的字节数组
 * @param offset 开始搜索的偏移位置
 * 
 * **zlib 魔数**：
 * - 0x78 0x9C: 默认压缩级别（最常用）
 * - 0x78 0x01: 无压缩/低压缩
 * - 0x78 0x5E: 最佳压缩
 * 
 * **工作原理**：
 * 1. 从指定偏移位置切片
 * 2. 创建 zlib 解码器
 * 3. 尝试解压并输出结果
 * 4. 成功后可得到标准 LRC 格式的歌词文本
 */
fn try_decompress_at_offset(bytes: &[u8], offset: usize) {
    use flate2::read::ZlibDecoder;
    use std::io::Read;

    // 边界检查：偏移量不能超过数组长度
    if offset >= bytes.len() {
        return;
    }

    println!("\n-- Trying decompression at offset {} --", offset);
    
    // 从偏移位置切片
    let slice = &bytes[offset..];
    
    // 创建 zlib 解码器
    let mut decoder = ZlibDecoder::new(slice);
    let mut decompressed = Vec::new();
    
    // 读取并解压数据
    match decoder.read_to_end(&mut decompressed) {
        Ok(_) => {
            // ✅ 解压成功
            println!("Decompressed {} bytes", decompressed.len());
            println!("Preview: {}", String::from_utf8_lossy(&decompressed[..decompressed.len().min(200)]));
        }
        Err(e) => {
            // ❌ 解压失败
            println!("Decompression failed: {}", e);
        }
    }
}

// ============================================================================
// 调试用的测试数据
// ============================================================================
// 这是一个示例 .qrc 文件的加密数据（十六进制格式）
// 在实际使用中，可以通过命令行参数传入其他从 .qrc 文件提取的数据
// 
// **数据来源**：
// 通常通过以下方式获取：
// 1. 从 QQ 音乐缓存目录找到 .qrc 文件
// 2. 读取文件内容为十六进制字符串
// 3. 传入此工具进行解密
const DEBUG_HEX: &str = "5B7E01E01CA39AC570D81F7282D678A13EFA969B88F913C7E9DDBF54871911F1C86D2AADF82D093E6069C7D3E3722349345D8CC272099118C33C7A6AEC60BE31133AD7498C5C762241D52093BB7EB6763B7B3B0245D4204DFAE282ADAE4A5C23860822C9EC543DBA3E6920116A00540A2D9763915B3D463612E10A691F01EC882B2E0A508E3E1DDAFBA80A1C0715CD7C64EAA69CAE23BBE2A277767DEEDA04A6A3B4EC2CF4F8394807BE277C4465C6A73676E2703D107CA69EB1B6B0D30E26540BB99EA30502A5C3A1DE811E723A40C9ACA616011CB059036082509ABB49B2B932D00273498B168CE84DA16AECB01919171B04FF9025C4EA54B38565C022024D5F2FF1AB299E33F60F2CF1E9E9702A6832170798C998E7AD8B2EF82046CFA2965FE97A74D7BB9950EBD7C94341ADB8BC67CE2D00E1A1D270AB7F979D33DAD058D4ED88ABBCBEADCF24AD7526B159EA2FD7C8A7F05877C603651738FFCB0B9317FA7D6A16B45EFF298753CE797BD58F823A31F207D7E597A9841C0957FFA0DAB9996D4CCF792A0B73F64D7F64F863F50C0B7078A829C9B79F6D3DB52EDF04D9C8BC2BF65E533471CC4FBBA98C622998176C0BBDA1C5EDE2701F7B172DDB1899CE84BC52080AE9C128D3A36E7F4BB98447DB156786302B6AD42957D7123D1DA5278A608F29B29F0DAA959DFE3F7ED346B202BF67F6E0EDE8B0DA9B437E9870B0F58DAD832A63C40DA614A6D5E698282AC6F94318798A7F65DC38EBAFC447357F02002D5B30A304344125E4E5EF43D267E17A838BF43345F3D9906579C5F4B36A627E5C9C23DB195C5268322A62B3A49568C7777A5DC79AC38F35DC1327A48ED0EC5EED15D0F30AB5032397BD0FC46A3082C8009C5B51B86FAE52BAA058A4257F04AF514BB56A1C4B5D8F821DBB46A68BABE4823309CD6F16F0B85D2D3F45282D743E581DF855B0B4CCF59EF445A06276B11B8B59CE4FFDB3457A08D688CDFA0E4216ABF0A449B8F76A76DF0222CBE422A6C209429D61DF4F3D766DB054DB2F49390B0C6F62028A6223E04445CF4DCC477DDF0647124107C121DB6BAF4ECA50C8163F3BCBD59CABBFD60780A5A4DEE0D5B0";

// ============================================================================
// 主函数
// ============================================================================
/**
 * 程序入口点
 * 
 * **执行流程**：
 * 1. 解析命令行参数（如果有则使用，否则使用调试数据）
 * 2. 将十六进制字符串转换为字节数组
 * 3. 调用 qq_qrc_decoder 库进行解密
 * 4. 在解密后的数据中搜索 zlib 压缩头
 * 5. 尝试在每个找到的位置解压缩数据
 * 
 * **输入**：
 * - 从 .qrc 文件读取的十六进制字符串
 * 
 * **输出**：
 * - 解密后的 LRC 格式歌词文本
 * 
 * **使用方法**：
 * ```bash
 * # 使用默认调试数据
 * cargo run
 * 
 * # 使用自定义 .qrc 文件内容
 * cargo run "5B7E01E0..."
 * ```
 */
fn main() {
    // Step 1: 获取命令行参数
    let arg = env::args().nth(1);  // 第一个参数
    let hex = arg.unwrap_or_else(|| DEBUG_HEX.to_string());  // 如果没有参数则使用调试数据

    // Step 2: 将十六进制字符串转换为字节数组
    let encrypted = match hex_to_bytes(&hex) {
        Some(b) => b,  // ✅ 转换成功
        None => {
            // ❌ 转换失败（无效的十六进制格式）
            eprintln!("Invalid hex string");
            std::process::exit(1);
        }
    };

    // Step 3: 输出加密数据信息
    println!("Encrypted bytes: {}", encrypted.len());  // 总字节数
    print_hex_preview(&encrypted, 64);  // 打印前 64 字节的十六进制预览

    // Step 4: 调用库函数进行解密
    let decrypted = match qq_qrc_decoder::decrypt_qrc_hex_to_string(&hex) {
        Ok(s) => s,  // ✅ 解密成功
        Err(e) => {
            // ❌ 解密失败
            println!("Decrypt failed: {}", e);
            std::process::exit(2);
        }
    };

    // Step 5: 输出解密结果
    println!("\nDecrypted output length: {}", decrypted.len());
    println!("Decrypted preview: {}", decrypted.chars().take(200).collect::<String>());

    // Step 6: 在解密后的原始字节中查找 zlib 压缩头
    // zlib 数据的魔数（Magic Number）通常是 0x78 开头
    let raw = decrypted.into_bytes();
    let mut candidates = vec![];  // 存储所有可能的 zlib 头位置
    
    // 遍历所有字节，查找 zlib 头特征
    for i in 0..raw.len().saturating_sub(1) {
        // zlib 头的特征：0x78 后面跟着 0x9C、0x01 或 0x5E
        // 这些是 zlib 压缩数据的标识符
        if raw[i] == 0x78 && (raw[i + 1] == 0x9C || raw[i + 1] == 0x01 || raw[i + 1] == 0x5E) {
            candidates.push(i);  // 记录偏移位置
        }
    }

    // Step 7: 输出找到的 zlib 头数量
    println!("Found {} zlib-like header candidates", candidates.len());
    
    // Step 8: 尝试在前 10 个位置解压缩（避免太多输出）
    for &offset in candidates.iter().take(10) {
        try_decompress_at_offset(&raw, offset);
    }
}
