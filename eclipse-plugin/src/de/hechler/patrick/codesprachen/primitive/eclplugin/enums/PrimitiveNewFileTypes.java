package de.hechler.patrick.codesprachen.primitive.eclplugin.enums;

public enum PrimitiveNewFileTypes {
		nft_empty_file,
		nft_super__main_lib_file,
		nft_main_file,
		nft_standalone__main_file,
	;
	@Override
	public String toString() {
		return name().substring(4).replace("__", "-").replace('_', ' ');
	}
	
}
