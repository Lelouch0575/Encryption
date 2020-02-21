package core;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * �ṩ�����ϣֵ���ܣ�ʹ��SHA-512�㷨
 */
public class HashUtil {
	//����salt�Ĺ�ϣֵ����
	public static byte[] encodeSHA512(byte[] data) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		return md.digest(data);
	}
	
	//��salt�Ĺ�ϣֵ����
	public static byte[] encodeSHA512(byte[] data,byte[] salt) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		
		byte[] dataWithSalt = new byte[data.length + salt.length];
		
		//��data��salt�����������ϵ�datawithsalt������
		int i=0;
		for(int j=0; j<data.length; j++) {
			dataWithSalt[i++] = data[j];
		}
		for(int j=0;j<salt.length;j++) {
			dataWithSalt[i++] = salt[j];
		}
		
		return md.digest(dataWithSalt);
	}
	
	//����salt
	public static byte[] getSalt() {
		byte[] salt = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(salt);
		return salt;
	}
}
