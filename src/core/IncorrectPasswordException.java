package core;

/**
 *���벻��ȷ�쳣
 */
public class IncorrectPasswordException extends Exception{
	public IncorrectPasswordException() {}
	public IncorrectPasswordException(String msg) {
		super(msg);
	}
}
