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
 * �ṩAES���ܽ��ܹ��ܣ��ɼ��ܽ����ַ������ļ�
 */
public class AESUtil {
	private static final String ALGORITHM = "AES";
	private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static final String CHARSET = "UTF-8";
	
	
	/**
	 * ���û����������ת��Ϊ��Կ
	 * @param password ����
	 * @return ��Կ
	 * @throws Exception
	 */
	public static SecretKeySpec getKey(String password) throws Exception {
		//������תΪ�ֽ�����
		byte[] passwordBuf = password.getBytes(CHARSET);
		//128λ������Կ������Ԫ��Ĭ�ϳ�ʼֵΪ0
		byte[] keyBuf = new byte[16];
		
		//���벻��16�ֽ���0������16�ֽ���ֻȡǰ16�ֽ�
		for(int i = 0; i < 16 && i < passwordBuf.length; i++) {
			keyBuf[i] = passwordBuf[i];
		}
		
		SecretKeySpec keySpec = new SecretKeySpec(keyBuf, ALGORITHM);
		return keySpec;
	}
	
	/**
	 * ��ȡ������ɵĳ�ʼ������
	 * @return ��ʼ���������ֽ�����
	 */
	public static byte[] getIv() {
		byte[] iv = new byte[16];
		SecureRandom random = new SecureRandom();
		random.nextBytes(iv);
		return iv;
	}
	
	/**
	 * �����ַ���
	 * @param plainText �ַ�������
	 * @param keySpec ��Կ
	 * @param ivSpec ��ʼ������
	 * @return ��base64������ַ�������
	 * @throws Exception
	 */
	public static String encrypt(String plainText,SecretKeySpec keySpec,IvParameterSpec ivSpec) throws Exception {
		
		//��ȡ�����㷨ʵ��
		Cipher cip = Cipher.getInstance(TRANSFORMATION);
		cip.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);
		
		byte[] plainTextBuf = plainText.getBytes(CHARSET);
		byte[] ciphetTextBuf = cip.doFinal(plainTextBuf);
		
		return Base64.getUrlEncoder().encodeToString(ciphetTextBuf);
	}
	
	/**
	 * �����ַ���
	 * @param cipherText ��base64������ַ�������
	 * @param keySpec ��Կ
	 * @param ivSpec ��ʼ������
	 * @return �ַ�������
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
	 * @param path �ļ��ľ���·��
	 * @param password ����
	 * @param isEncryptFileName �Ƿ�����ļ���
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
			//�����ļ���
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
		
		outFile.write(salt); //д��saltֵ��16�ֽ�
		outFile.write(hashWithSalt);  //д��hashֵ��64�ֽ�
		outFile.write(iv);  //д���ʼ��������16�ֽ�
		
		Cipher cip = Cipher.getInstance(TRANSFORMATION);
		cip.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);
		
		crypt(inFile,outFile,cip);
		
		inFile.close();
		outFile.flush();
		outFile.close();
		
		file.delete();  //������ɺ�ɾ��Դ�ļ�
	}
	
	/**
	 * @param path �ļ��ľ���·��
	 * @param password ����
	 * @param isDecryptFileName �Ƿ�����ļ���
	 * @throws IncorrectPasswordException ��������쳣
	 * @throws Exception
	 */
	public static void decrypt(String path,String password,boolean isDecryptFileName) throws IncorrectPasswordException,Exception {

		FileInputStream inFile = new FileInputStream(path);
		File file = new File(path);
		
		byte[] salt = new byte[16];
		byte[] hashWithSalt = new byte[64];
		byte[] iv = new byte[16];
		
		//���ܵ��ļ�ǰ96�ֽ�Ϊ��saltֵ��hashֵ�ͳ�ʼ������
		inFile.read(salt);  //salt 16�ֽ�
		inFile.read(hashWithSalt);  //hash 64�ֽ�
		inFile.read(iv);  //��ʼ������ 16�ֽ�
		
		//�����û����뼰��ȡ��saltֵ���¼���hashֵ
		byte[] passwordHashWithSalt = HashUtil.encodeSHA512(password.getBytes(CHARSET), salt);
		
		if(!MessageDigest.isEqual(hashWithSalt, passwordHashWithSalt)) {
			//���¼���Ĺ�ϣֵ����ļ��ж�ȡ���Ĺ�ϣֵ��ƥ�䣬˵�����벻��ȷ
			System.out.println("Incorrect password !");
			inFile.close();
			throw new IncorrectPasswordException();
		}
		System.out.println("Correct password !");
		
		SecretKeySpec keySpec = getKey(password);
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		
		String outPath;
		if(isDecryptFileName) {
			//�����ļ���
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
	 * �ļ����ܽ��ܵĺ��Ĳ���
	 * @param inFile �ļ�������
	 * @param outFile �ļ������
	 * @param cip �ѳ�ʼ����Cipher����
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
