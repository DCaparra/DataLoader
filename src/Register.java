package org.dataloader;

import java.util.HashMap;

public class Register
{
  private String Design=null;
  private HashMap<String, String> DataArray=null;

  public Register()
  {
    DataArray=new HashMap<String, String>();
  }

  public void SetDesign(String d)
  {
    Design=d;
  }

  public String GetDesign()
  {
    return Design;
  }

  public void PutItem(String key, String value)
  {
    DataArray.put(key, value);
  }

  public HashMap<String, String> GetItems()
  {
    return DataArray;
  }
}
