package core;

import java.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 提供AES加密解密功能，可加密解密字符串或文件
 */
public class AESUtil {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String CHARSET = "UTF-8";
	
	
	/**
	 * 将用户输入的密码转换为密钥
	 * @param password 密码
	 * @return 密钥
	 * @throws Exception
	 */
	public static SecretKeySpec getKey(String password) throws Exception {
		//将密码转为字节数组
		byte[] passwordBuf = password.getBytes(CHARSET);
		//128位长度密钥，数组元素默认初始值为0
		byte[] keyBuf = new byte[16];
		
		//密码不足16字节则补0，超过16字节则只取前16字节
		for(int i = 0; i < 16 && i < passwordBuf.length; i++) {
			keyBuf[i] = passwordBuf[i];
		}
		
		SecretKeySpec keySpec = new SecretKeySpec(keyBuf, ALGORITHM);
		return keySpec;
	}
	
	/**
	 * 获取随机生成的初始化向量
	 * @return 初始化向量的字节数组
	 */
	public static byte[] getIv() {
		byte[] iv = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		return iv;
	}
	
	/**
	 * 加密字符串
	 * @param plainText 字符串明文
	 * @param keySpec 密钥
	 * @param ivSpec 初始化向量
	 * @return 经base64编码的字符串密文
	 * @throws Exception
	 */
	public static String encrypt(String plainText,SecretKeySpec keySpec,IvParameterSpec ivSpec) throws Exception {
		
		//获取加密算法实例
		Cipher cip = Cipher.getInstance(TRANSFORMATION);
		cip.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);
		
		byte[] plainTextBuf = plainText.getBytes(CHARSET);
		byte[] ciphetTextBuf = cip.doFinal(plainTextBuf);
		
		return Base64.getUrlEncoder().encodeToString(ciphetTextBuf);
	}
	
	/**
	 * 解密字符串
	 * @param cipherText 经base64编码的字符串密文
	 * @param keySpec 密钥
	 * @param ivSpec 初始化向量
	 * @return 字符串明文
	 * @throws Exception
	 */
	public static String decrypt(String cipherText,SecretKeySpec keySpec,IvParameterSpec ivSpec) throws Exception {
		Cipher cip = Cipher.getInstance(TRANSFORMATION);
		cip.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);
		
		byte[] cipherTextBuf = Base64.getUrlDecoder().decode(cipherText);
		byte[] plainTextBuf = cip.doFinal(cipherTextBuf);
		
		return new String(plainTextBuf, CHARSET);
	}
	
	
	
	/**
	 * @param path 文件的绝对路径
	 * @param password 密码
	 * @param isEncryptFileName 是否加密文件名
	 * @throws Exception
	 */
	public static void encrypt(String path,String password,boolean isEncryptFileName) throws Exception {
		
		byte[] iv = getIv();
		SecretKeySpec keySpec = getKey(password);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		
		FileInputStream inFile = new FileInputStream(path);
		File file = new File(path);
		
		String outPath;
		if(isEncryptFileName) {
			//加密文件名
			String parentName = file.getParent();
			String name = file.getName();
			String encryptedName = encrypt(name, keySpec, ivSpec);
			outPath = parentName + "/" + encryptedName + ".crypt";
		} else {
			outPath = path + ".crypt";
		}
		
		System.out.println("Encrypt out: " + outPath);
		
		FileOutputStream outFile = new FileOutputStream(outPath);
		
		byte[] salt = HashUtil.getSalt();
		byte[] hashWithSalt = HashUtil.encodeSHA512(password.getBytes(CHARSET), salt);
		
		outFile.write(salt); //写入salt值，16字节
		outFile.write(hashWithSalt);  //写入hash值，64字节
		outFile.write(iv);  //写入初始化向量，16字节
		
		Cipher cip = Cipher.getInstance(TRANSFORMATION);
		cip.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);
		
		crypt(inFile,outFile,cip);
		
		inFile.close();
		outFile.flush();
		outFile.close();
		
		file.delete();  //加密完成后删除源文件
	}
	
	/**
	 * @param path 文件的绝对路径
	 * @param password 密码
	 * @param isDecryptFileName 是否加密文件名
	 * @throws IncorrectPasswordException 密码错误异常
	 * @throws Exception
	 */
	public static void decrypt(String path,String password,boolean isDecryptFileName) throws IncorrectPasswordException,Exception {

		FileInputStream inFile = new FileInputStream(path);
		File file = new File(path);
		
		byte[] salt = new byte[16];
		byte[] hashWithSalt = new byte[64];
		byte[] iv = new byte[16];
		
		//加密的文件前96字节为：salt值、hash值和初始化向量
		inFile.read(salt);  //salt 16字节
		inFile.read(hashWithSalt);  //hash 64字节
		inFile.read(iv);  //初始化向量 16字节
		
		//根据用户密码及读取的salt值重新计算hash值
		byte[] passwordHashWithSalt = HashUtil.encodeSHA512(password.getBytes(CHARSET), salt);
		
		if(!MessageDigest.isEqual(hashWithSalt, passwordHashWithSalt)) {
			//重新计算的哈希值与从文件中读取到的哈希值不匹配，说明密码不正确
			System.out.println("Incorrect password !");
			inFile.close();
			throw new IncorrectPasswordException();
		}
		System.out.println("Correct password !");
		
		SecretKeySpec keySpec = getKey(password);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		
		String outPath;
		if(isDecryptFileName) {
			//解密文件名
			String parentName = file.getParent();
			String name = file.getName();
			String decryptedName = decrypt(name.substring(0, name.length() - 6), keySpec, ivSpec);
			outPath = parentName + "/" + decryptedName;
		}
		else {
			outPath = path.substring(0, path.length() - 6);
		}
		
		System.out.println("Decrypt out: " + outPath);
		
		FileOutputStream outFile = new FileOutputStream(outPath);
		
		Cipher cip = Cipher.getInstance(TRANSFORMATION);
		cip.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);
		
		crypt(inFile, outFile, cip);
		
		inFile.close();
		outFile.flush();
		outFile.close();
		
		file.delete();
	}
	
	/**
	 * 文件加密解密的核心操作
	 * @param inFile 文件输入流
	 * @param outFile 文件输出流
	 * @param cip 已初始化的Cipher对象
	 * @throws Exception
	 */
	public static void crypt(FileInputStream inFile,FileOutputStream outFile,Cipher cip) throws Exception {
		
		int bufSize = 8192;
		byte[] inBuffer = new byte[bufSize];
		byte[] outBuffer;
		
		int inLength = 0;
		
		while(true) {
			inLength = inFile.read(inBuffer);
			if(inLength == bufSize) {
				outBuffer = cip.update(inBuffer,0,bufSize);
				outFile.write(outBuffer);
			}else
				break;
		}
		if(inLength > 0)
			outBuffer = cip.doFinal(inBuffer,0,inLength);
		else
			outBuffer = cip.doFinal();

		outFile.write(outBuffer);
	}
}
