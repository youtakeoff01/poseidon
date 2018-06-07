package com.hand.bdss.dsmp.model;


import java.io.Serializable;

public class DocumentInfo implements Serializable {
  private static final long serialVersionUID = -1391671309646014634L;
  
  private String srcPath;
  
  private String dstPath;
  
  private String fileName;
  
  private String isDirectory;

  public String getSrcPath()
  {
    return this.srcPath;
  }

  public void setSrcPath(String srcPath) {
    this.srcPath = srcPath;
  }

  public String getDstPath() {
    return this.dstPath;
  }

  public void setDstPath(String dstPath) {
    this.dstPath = dstPath;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  
  public String getIsDirectory() {
	return isDirectory;
}

public void setIsDirectory(String isDirectory) {
	this.isDirectory = isDirectory;
}

public String toString(){
	return "DocumentInfo : srcPath=" + srcPath 
			+ ",dstPath=" + dstPath + ",fileName="
			+ fileName;
	  
  }
}