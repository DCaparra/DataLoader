package org.dataloader;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;

public class Trf
{
  private String data=null;
  private int DataType=1;
  private int Escala=0;
  private String SepDecimal=null;
  private String FechaFormato=null;
  private int Trim=0;
  private int TipoRep=0;
  private int Pos=0;
  private HashMap<String, String> Replace=null;
  private ArrayList<Integer> Rangos=new ArrayList<Integer> ();

  public Trf(Element eTrf) throws Exception
  {
    this.DataType=Integer.parseInt(eTrf.getElementsByTagName("DataType").item(0).getTextContent());
    this.Trim=Integer.parseInt(eTrf.getElementsByTagName("Trim").item(0).getTextContent());

    if (DataType == 3) {
      this.Escala=Integer.parseInt(eTrf.getElementsByTagName("Escala").item(0).getTextContent());
      this.SepDecimal=eTrf.getElementsByTagName("SepDecimal").item(0).getTextContent();
    }

    if (DataType == 4 || DataType == 5 || DataType == 6) {
      this.FechaFormato=eTrf.getElementsByTagName("FechaFormato").item(0).getTextContent();
    }

      //this.Pos=Integer.parseInt(eTrf.getElementsByTagName("Pos").item(0).getTextContent());

    this.TipoRep=Integer.parseInt(eTrf.getElementsByTagName("TipoRep").item(0).getTextContent());
    if (TipoRep > 0) {
      Replace=new HashMap<String, String>();

      NodeList nlReplace =  eTrf.getElementsByTagName("Replace");
      for (int r = 0; r < nlReplace.getLength(); r++) {
        Node nReplace = nlReplace.item(r);

        if (nReplace.getNodeType() == Node.ELEMENT_NODE) {
          Element eReplace = (Element) nReplace;
          Replace.put(eReplace.getElementsByTagName("calve").item(0).getTextContent(), eReplace.getElementsByTagName("valor").item(0).getTextContent());
        }
      }

      if (Replace!=null){
        for(Map.Entry<String, String> entry : Replace.entrySet()) {
          String key=entry.getKey();
          if(!Rangos.contains(key.length())){
            Rangos.add(key.length());
          }
        }
        Collections.sort(Rangos, Collections.reverseOrder() );
      }
    }
  }

  public String GetValue(String data) throws Exception
  {
    String result=null;
    //Trim
    if (Trim>0){
      switch (Trim) {
      case 1:
        //Derecha e izquierda
        data=data.trim();
        break;
      case 2:
        //Derecha
        data = data.replaceAll("\\s+$","");
        break;
      case 3:
        //Izquierda
        data = data.replaceAll("^\\s+","");
        break;
      }
    }

    //Replace
    if (TipoRep>0){
      switch (TipoRep) {
      case 1:
        //Coincidencia palabra completa
        
        break;
      case 2:
        //Todas las ocurrencias
        
        break;
      case 3:
        //Solo inicio(Prefijo)
        
        break;
      case 4:
        //Solo Fin(Sufijo)
        String buscado=null;
        int len=data.length();
        for (int r : Rangos) {
          if (len >= r) {
            buscado=data.substring(len-r, len);
            if(Replace.containsKey(buscado)) {
              data=data.substring(0, len-r) + Replace.get(buscado);
              break;
            }
          }
        }
        break;
      case 5:
        //Posicion Estatica der a izq

        break;
      }
    }

    //Formatos propios de los tipos de datos
    try
    {
      switch (DataType) {
      case 1:
        //Char
        result = data;
        break;
      case 2:
        //Int
        if (!data.equals("")) {

          //Reorganiza signo
          if (data.contains("-")){
            result="-"+data.replace("-","");
          } else {
            result=data;
          }
        } else {
          result=null;
        }
        break;
      case 3:
        //Double
        if (!data.equals("")) {

          //Reorganiza signo
          if (data.contains("-")){
            result="-"+data.replace("-","");
          }
          //Arregla coma
          if (data.contains(SepDecimal) && (SepDecimal.length()>0)){
            if(SepDecimal==","){
              result=data.replace(".","");
              result=data.replace(",",".");
            } else {
              result=data.replace(",","");
            }
          } else {
            result=data.substring(0, data.length()-Escala) + "." + data.substring(data.length()-Escala, data.length());
          }
        } else {
          result=null;
        }
        break;
      case 4:
        //Date
        if (!data.equals("")) {
          DateFormat dfid = new SimpleDateFormat(FechaFormato);
          DateFormat dfod = new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'");
          result = dfod.format(dfid.parse(data));
        } else {
          result=null;
        }
        break;
      case 5:   
        //Time
        if (!data.equals("")) {
          DateFormat dfit = new SimpleDateFormat(FechaFormato);
          DateFormat dfot = new SimpleDateFormat("HH:mm:ss");
          result = dfot.format(dfit.parse(data));
        } else {
          result=null;
        }
        break;
      case 6: 
        //DateTime
        if (!data.equals("")) {
          DateFormat dfidt = new SimpleDateFormat(FechaFormato);
          DateFormat dfodt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
          result = dfodt.format(dfidt.parse(data));
        } else {
          result=null;
        }
        break;
      }
    }
    catch (Exception e) {throw e;}
 
    return result;
  }
}
