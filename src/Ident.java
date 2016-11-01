package org.dataloader;

import org.w3c.dom.Element;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ident
{
  private static String RegData=null;
  private static int RegNum=0;
  private static boolean Ultimo=false;

  private int TipoIdent=0;
  private int CodigoPos=0;
  private int RegDesde=0;
  private int RegHasta=0;
  private int ClaveCampo=0;
  private int ClaveDesde=0;
  private int ClaveHasta=0;
  private String Claves=null;
  private boolean Match=true;
  private String Regex=null;
  private int EstReg=0;
  private String RegDelim=null;
  private Pattern pattern=null;

  Ident(Element eIdent, int EstReg, String RegDelim) throws Exception
  {
    this.TipoIdent=Integer.parseInt(eIdent.getElementsByTagName("TipoIdent").item(0).getTextContent());

    //Codigo
    if (TipoIdent==1) {
      this.Claves=eIdent.getElementsByTagName("Claves").item(0).getTextContent();
      if (EstReg==1) {
        this.ClaveDesde=Integer.parseInt(eIdent.getElementsByTagName("ClaveDesde").item(0).getTextContent());
        this.ClaveHasta=Integer.parseInt(eIdent.getElementsByTagName("ClaveHasta").item(0).getTextContent());
      }
      if (EstReg==2) {
        this.ClaveCampo=Integer.parseInt(eIdent.getElementsByTagName("ClaveCampo").item(0).getTextContent());
      }
    }

    //PosReg
    if (TipoIdent==2) {
      this.CodigoPos=Integer.parseInt(eIdent.getElementsByTagName("CodigoPos").item(0).getTextContent());
    }

    //Rango
    if (TipoIdent==3) {
      this.RegDesde=Integer.parseInt(eIdent.getElementsByTagName("RegDesde").item(0).getTextContent());
      this.RegHasta=Integer.parseInt(eIdent.getElementsByTagName("RegHasta").item(0).getTextContent());
    }

    //Regex
    if (TipoIdent==4) {
      this.Match=Boolean.parseBoolean(eIdent.getElementsByTagName("Match").item(0).getTextContent());
      this.Regex=eIdent.getElementsByTagName("Regex").item(0).getTextContent();
      this.pattern = Pattern.compile(Regex);
    }

    this.EstReg=EstReg;
    this.RegDelim=RegDelim;
  }

  public static void SetRegistro(String reg, int regnum, boolean ultimo)
  {
    RegData=reg;
    RegNum=regnum;
    Ultimo=ultimo;
  }

  public boolean Valor()
  {
    boolean result=false;

    if (TipoIdent==1) //Codigo
    {
      if (Claves.length()>0)
      {
        String regclave=null;
        String[] ClavesSplit=Claves.split("\\|");

        if (EstReg==1) regclave=SubStr(RegData, ClaveDesde, ClaveHasta);

        if (EstReg==2){
          String [] RegSplit = null;
          RegSplit=RegData.split(RegDelim);
          regclave=RegSplit[ClaveCampo];
        }

        for (int i=0; i < ClavesSplit.length; i++) {
          if(regclave.equals(ClavesSplit[i])) result=true;
        }
      }
    }

    if (TipoIdent==2) //PosReg
    {
      if (CodigoPos==1) result=true; //Todas
      if (CodigoPos==2 && RegNum!=1) result=true; //Todas - Primera
      if (CodigoPos==3 && Ultimo==false) result=true; //Todas - Ultima
      if (CodigoPos==4 && RegNum!=1 && Ultimo==false) result=true; //Todas - Primera y Ultima
      if (CodigoPos==5 && RegNum==1) result=true; //Solo Primera
      if (CodigoPos==6 && Ultimo==true) result=true; //Solo Ultima
    }

    if (TipoIdent==3) //Rango
    {
      if(RegNum>=RegDesde && RegNum<=RegHasta) result=true;
    }

    if (TipoIdent==4) //Regex
    {
      Matcher matcher = pattern.matcher(RegData);
      if (Match==matcher.find()) {result=true;}
    }

    return result;
  }

  private String SubStr(String data, int desde, int hasta)
  {
    String str=null;

    if (data.length() > 0) {
      if (hasta > data.length()) {hasta=data.length();}
      if (desde > data.length()) {desde=data.length();}
      CharSequence cs = data.subSequence(desde-1, hasta);
      str = cs.toString();
    } else {
      str="";
    }
    return str;
  }
}
