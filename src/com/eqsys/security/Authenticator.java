package com.eqsys.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class Authenticator {
	
	private static final String HEX_NUMS_STR = "0123456789ABCDEF";
	private static final Integer SALT_LENGTH = 12;
	
	//注册认证密码
	public static final String AUTHENCODE = "PASSWORD";

	/**
	 * 将16进制字符串转换成字节数组
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] hexChars = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));
		}
		return result;
	}

	/**
	 * 将指定byte数组转换成16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			hexString.append(hex.toUpperCase());
		}
		return hexString.toString();
	}

	/**
	 * 验证口令是否合法
	 * 
	 * @param password
	 * @param passwordInDb
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean validMD5String(String password, String passwordInDb) {
		// 将16进制字符串格式口令转换成字节数组
		byte[] pwdInDb = hexStringToByte(passwordInDb);
		byte[] salt = new byte[SALT_LENGTH];
		try{
		System.arraycopy(pwdInDb, 0, salt, 0, SALT_LENGTH);
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(salt);
		md.update(password.getBytes("UTF-8"));
		byte[] digest = md.digest();
		byte[] digestInDb = new byte[pwdInDb.length - SALT_LENGTH];
		System.arraycopy(pwdInDb, SALT_LENGTH, digestInDb, 0, digestInDb.length);
		if (Arrays.equals(digest, digestInDb)) {
			return true;
		} else {
			return false;
		}
		}catch(Exception e){
			//异常则直接对比字符串
			System.out.println("validMD5String -----"+e.getMessage());
			if(password.equals(passwordInDb)){
				return true;
			}else{
				return false;
			}
		}
	}

	/**
	 * 获得加密后的16进制形式口令
	 * 
	 * @param password
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String getEncryptedString(String password) {
		// 声明加密后的口令数组变量
		byte[] pwd = null;
		// 随机数生成器
		SecureRandom random = new SecureRandom();
		// 声明盐数组变量
		byte[] salt = new byte[SALT_LENGTH];
		// 将随机数放入盐变量中
		random.nextBytes(salt);

		// 声明消息摘要对象
		MessageDigest md = null;
		try {
			// 创建消息摘要
			md = MessageDigest.getInstance("MD5");
			// 将盐数据传入消息摘要对象
			md.update(salt);
			// 将口令的数据传给消息摘要对象
			md.update(password.getBytes("UTF-8"));
			// 获得消息摘要的字节数组
			byte[] digest = md.digest();

			// 因为要在口令的字节数组中存放盐，所以加上盐的字节长度
			pwd = new byte[digest.length + SALT_LENGTH];
			// 将盐的字节拷贝到生成的加密口令字节数组的前12个字节，以便在验证口令时取出盐
			System.arraycopy(salt, 0, pwd, 0, SALT_LENGTH);
			// 将消息摘要拷贝到加密口令字节数组从第13个字节开始的字节
			System.arraycopy(digest, 0, pwd, SALT_LENGTH, digest.length);
		} catch (Exception e) {
			//异常则不用编码
			System.out.println("getEncryptedString -----"+e.getMessage());
			pwd = password.getBytes();
		}
		// 将字节数组格式加密后的口令转化为16进制字符串格式的口令
		return byteToHexString(pwd);
	}
	
	/** 验证注册包的认证码 */
	public static boolean validateReg(String authenCode){
		if(authenCode != null && !"".equals(authenCode) && AUTHENCODE.equals(authenCode)){
			return true;
		}
		return false;
	}
}
