package org.dataloader;

import java.io.StringWriter;
import java.io.PrintWriter;

public class MTCom
{
  private boolean parar=false;
  private int proccount=0;
  private boolean finprc=false;
  private MTCom mtcprod=null;
  private boolean errflag=false;
  private String errDesc="";
  private String errStack="";
  public MTCom()
  {

  }

  public MTCom(MTCom Productor)
  {
    mtcprod=Productor;
  }

  public void Parar()
  {
    parar=true;
  }

  public boolean Continuar()
  {
    return !parar;
  }

  public boolean FinProc()
  {
    return finprc;
  }

  public void setFinProc(boolean fin)
  {
    finprc=fin;
  }

  public boolean FinProd()
  {
    boolean finprd=true;
    if (mtcprod!=null) {finprd=mtcprod.FinProc();}
    return finprd;
  }

  public void setError(Exception e)
  {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    errStack=sw.toString();
    errDesc=errStack.split("\\n+")[0] + errStack.split("\\n+")[1];
    errflag=true;
  }

  public boolean Error()
  {
    return errflag;
  }

  public String getErrorDesc()
  {
    return errDesc;
  }

  public String getErrorStack()
  {
    return errStack;
  }

  public int getCount()
  {
    return proccount;
  }

  public void setCountMasUno()
  {
    proccount=proccount+1;
  }
}
