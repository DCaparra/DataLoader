package org.dataloader;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Map
{
  private static String RegData=null;

  private String Campo=null;
  private int PosRel=0;
  private int PosDesde=0;
  private int PosHasta=0;
  private int EstReg=0;
  private String RegDelim=null;
  private Trf cTrf=null;

  Map(Element eMap, int EstReg, String RegDelim) throws Exception
  {
    this.Campo=eMap.getElementsByTagName("Campo").item(0).getTextContent();

    if (EstReg==1) {
      this.PosDesde=Integer.parseInt(eMap.getElementsByTagName("PosDesde").item(0).getTextContent());
      this.PosHasta=Integer.parseInt(eMap.getElementsByTagName("PosHasta").item(0).getTextContent());
    }

    if (EstReg==2) {
      this.PosRel=Integer.parseInt(eMap.getElementsByTagName("PosRel").item(0).getTextContent());
    }

    this.EstReg=EstReg;
    this.RegDelim=RegDelim;

    NodeList nlTrf =  eMap.getElementsByTagName("trf");
    Node nTrf = nlTrf.item(0);

    if (nTrf.getNodeType() == Node.ELEMENT_NODE) {
      Element eTrf = (Element) nTrf;
      cTrf=new Trf(eTrf);
    }
  }

  public static void SetRegistro(String reg)
  {
    RegData=reg;
  }

  public String GetValue() throws Exception
  {
    String result=null;
    String data=null;

    if(EstReg==1) {
      data=SubStr(RegData, PosDesde, PosHasta);
    }
    if(EstReg==2) {
      if (PosRel==0){
        data=RegData;
      } else {
        String [] datasplit = null;
        datasplit=RegData.split(RegDelim);
        if (datasplit.length>=PosRel) {data=datasplit[PosRel-1];} else {data="";}
      }
    }

    result=cTrf.GetValue(data);
    return result;
  }

  public String GetField()
  {
    return Campo;
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
