package core;

/**
 *密码不正确异常
 */
public class IncorrectPasswordException extends Exception{
	public IncorrectPasswordException() {}
	public IncorrectPasswordException(String msg) {
		super(msg);
	}
}
