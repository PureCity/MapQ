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
		int type = (int)Math.round(Math.random()*(100 - 0) + 0) % 3;//只有数字,大写字母,小写字母三种类型
		if (type == 0) {
			val = (int)Math.round(Math.random() *(57 - 48) + 48);//0 - 9的ASCII码
		}else if(type == 1){
			val = (int)Math.round(Math.random() *(90 - 65) + 65);//A - Z的ASCII码
		}else if(type == 2){
			val = (int)Math.round(Math.random() *(122 - 97) + 97);//a - z的ASCII码
		}
		return val;
	}

	public String getCodeString() {
		return codeString;
	}
	
	
}
