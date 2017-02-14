package org.dataloader;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.util.ArrayList;

public class Map
{
  private String RegData=null;

  private String Campo=null;
  private int Fuente=0;
  private String ValorFijo=null;
  private ArrayList<int[]> PosRel = new ArrayList<int[]>();
  private int PosDesde=0;
  private int PosHasta=0;
  private int EstReg=0;
  private String [] RegDelim=null;
  private Trf cTrf=null;
  private int RegNum=0;

  Map(Element eMap, int EstReg, String RegDelim) throws Exception
  {
    this.Campo=eMap.getElementsByTagName("Campo").item(0).getTextContent();
    this.Fuente=Integer.parseInt(eMap.getElementsByTagName("Fuente").item(0).getTextContent());

    if (Fuente==1) {
      if (EstReg==1) {
        this.PosDesde=Integer.parseInt(eMap.getElementsByTagName("PosDesde").item(0).getTextContent());
        this.PosHasta=Integer.parseInt(eMap.getElementsByTagName("PosHasta").item(0).getTextContent());
      }

      if (EstReg==2) {
        String[] strArraySuma = eMap.getElementsByTagName("PosRel").item(0).getTextContent().split("\\+");
        
        for(int s = 0; s < strArraySuma.length; s++) {
          String[] strArrayCampo = strArraySuma[s].split("\\.");
          int[] intArray = new int[strArrayCampo.length];
          for(int c = 0; c < strArrayCampo.length; c++) {
            intArray[c]=Integer.parseInt(strArrayCampo[c]);
          }
          this.PosRel.add(intArray); 
        }
        this.RegDelim=RegDelim.split("\\.");
      }
    }

    if (Fuente==3) {
      this.ValorFijo=eMap.getElementsByTagName("ValorFijo").item(0).getTextContent();
    }

    this.EstReg=EstReg;

    NodeList nlTrf =  eMap.getElementsByTagName("trf");
    Node nTrf = nlTrf.item(0);

    if (nTrf.getNodeType() == Node.ELEMENT_NODE) {
      Element eTrf = (Element) nTrf;
      cTrf=new Trf(eTrf);
    }
  }

  public void SetRegistro(String reg, int regnum)
  {
    RegData=reg;
    RegNum=regnum;
  }

  public String GetValue() throws Exception
  {
    String result=null;
    String data="";

    if (Fuente==1) {
      if(EstReg==1) {
        data=SubStr(RegData, PosDesde, PosHasta);
      }
      if(EstReg==2) {
        if (PosRel.get(0)[0]==0){
          data=RegData;
        } else {
          String [] fieldsplit = null;
          String field = null;
          
          for(int s = 0; s < PosRel.size(); s++) {
            field=RegData;
            for(int c = 0; c < PosRel.get(s).length; c++) {
              fieldsplit=field.split(RegDelim[c]);
              if (fieldsplit.length>=PosRel.get(s)[c]) {field=fieldsplit[PosRel.get(s)[c]-1];} else {field="";}
            }
            data=data+field;
          }
        }
      }
      result=cTrf.GetValue(data);
    }

    if (Fuente==2) {
      result=Integer.toString(RegNum);
    }

    if (Fuente==3) {
      result=ValorFijo;
    }

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
