package core;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * 提供计算哈希值功能，使用SHA-512算法
 */
public class HashUtil {
	//不带salt的哈希值计算
	public static byte[] encodeSHA512(byte[] data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		return md.digest(data);
	}
	
	//带salt的哈希值计算
	public static byte[] encodeSHA512(byte[] data,byte[] salt) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		
		byte[] dataWithSalt = new byte[data.length + salt.length];
		
		//将data和salt两个数组整合到datawithsalt数组中
		int i=0;
		for(int j=0; j<data.length; j++) {
			dataWithSalt[i++] = data[j];
		}
		for(int j=0;j<salt.length;j++) {
			dataWithSalt[i++] = salt[j];
		}
		
		return md.digest(dataWithSalt);
	}
	
	//生成salt
	public static byte[] getSalt() {
		byte[] salt = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(salt);
		return salt;
	}
}
