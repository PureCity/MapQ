package com.dlpu.tool;

public class CreateCode {
	
	private String codeString = null;
	
	public CreateCode(){
		updateCode();
	}
	
	public void updateCode(){
		char temp = ' ';
		temp = (char)getRandomASCII();
		this.codeString = temp +"";
		temp = (char)getRandomASCII();
		this.codeString = this.codeString + temp;
		temp = (char)getRandomASCII();
		this.codeString = this.codeString + temp;
		temp = (char)getRandomASCII();
		this.codeString = this.codeString + temp;
	}
	
	private int getRandomASCII(){
		int val = 0;
		int type = (int)Math.round(Math.random()*(100 - 0) + 0) % 3;//ֻ������,��д��ĸ,Сд��ĸ��������
		if (type == 0) {
			val = (int)Math.round(Math.random() *(57 - 48) + 48);//0 - 9��ASCII��
		}else if(type == 1){
			val = (int)Math.round(Math.random() *(90 - 65) + 65);//A - Z��ASCII��
		}else if(type == 2){
			val = (int)Math.round(Math.random() *(122 - 97) + 97);//a - z��ASCII��
		}
		return val;
	}

	public String getCodeString() {
		return codeString;
	}
	
	
}
