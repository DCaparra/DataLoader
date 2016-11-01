package org.dataloader;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class Design
{
  private static String RegData=null;
  private static int RegNum=0;
  private static boolean Ultimo=false;

  private String destino=null;
  private int EstReg=0;
  private String RegDelim=null;

  private ArrayList<Ident> Idents=new ArrayList<Ident> ();
  private ArrayList<Map> Maps=new ArrayList<Map> ();

  Design(Element eDesign, int EstReg, String RegDelim) throws Exception
  {
    //Lee parametros XML

    this.destino=eDesign.getElementsByTagName("Destino").item(0).getTextContent();
    //Carga coleccion identificadores
    NodeList nlIdent = eDesign.getElementsByTagName("ident");
    for (int i = 0; i < nlIdent.getLength(); i++) {
      Node nIdent = nlIdent.item(i);
      if (nIdent.getNodeType() == Node.ELEMENT_NODE) {
        Element eIdent = (Element) nIdent;
        Ident I=new Ident(eIdent, EstReg, RegDelim);
        Idents.add(I);
      }
    }

    //Carga coleccion maps
    NodeList nlMap =  eDesign.getElementsByTagName("map");
    for (int m = 0; m < nlMap.getLength(); m++) {
      Node nMap = nlMap.item(m);

      if (nMap.getNodeType() == Node.ELEMENT_NODE) {
        Element eMap = (Element) nMap;

        Map M=new Map(eMap, EstReg, RegDelim);
        Maps.add(M);
      }
    }
    //Fin Lee diseños
  }

  public static void SetLine(String reg, int regnum, boolean ultimo)
  {
    RegData=reg;
    RegNum=regnum;
    Ultimo=ultimo;
    Ident.SetRegistro(RegData, RegNum, Ultimo);
    Map.SetRegistro(RegData);
  }

  public boolean Identificado()
  {
    boolean result=true;
    for (Ident I : Idents) {
      if (I.Valor()==false) result=false;
    }
    return result;
  }

  public Register GetRegister() throws Exception
  {
    Register reg=new Register();

    reg.SetDesign(destino);
    for (Map M : Maps) {
      try {
        reg.PutItem(M.GetField(), M.GetValue());
      }
      catch (Exception e) {throw e;}
    }
    return reg;
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
