package org.dataloader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import java.util.HashMap;
import java.util.Map;

import java.io.*;
import java.nio.file.AccessDeniedException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.io.StringWriter;
import java.io.PrintWriter;

public class DataLoader
{

  private BlockingQueue<String> ReadQueue=null;
  private BlockingQueue<Register> InsertQueue=null;
  private HashMap<String, String> FixValues=null;
  private File fileorg=null;
  private File fXml=null;
  private MTCom mtcr=null;
  private MTCom mtcp=null;
  private MTCom mtci=null;

  public DataLoader (File fileorg, File fXml, HashMap<String, String> FixValues)
  {
    this.fileorg=fileorg;
    this.fXml=fXml;
    this.FixValues=FixValues;

  }

  public void LoadData() throws Exception
  {

    boolean error=false;
    String errmsg=null;
    int QueueLength=1000;
    int QueueSleep=50;

    mtcr = new MTCom();
    mtcp = new MTCom(mtcr);
    mtci = new MTCom(mtcp);

    try {
      if(fileorg.exists() == false || fileorg.isDirectory() == true)
        { throw new FileNotFoundException("Error: No se encontro el archivo " + fileorg); }      
      if (fileorg.canRead() == false)
        { throw new AccessDeniedException("Error: Permiso de lectura denegado sobre archivo " + fileorg); }

      if(fXml.exists() == false || fXml.isDirectory() == true)
        { throw new FileNotFoundException("Error: No se encontro el archivo " + fXml); }      
      if (fXml.canRead() == false)
        { throw new AccessDeniedException("Error: Permiso de lectura denegado sobre archivo " + fXml); }

      //Lee configuracion
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(fXml);

      doc.getDocumentElement().normalize();

      NodeList nlGlobal = doc.getElementsByTagName("global");
      NodeList nlReader = doc.getElementsByTagName("reader");
      NodeList nlProc = doc.getElementsByTagName("proc");
      NodeList nlInsert = doc.getElementsByTagName("insert");

      Node nGlobal = nlGlobal.item(0);
      Element eGlobal = (Element) nGlobal;
      QueueLength=Integer.parseInt(eGlobal.getElementsByTagName("queuelength").item(0).getTextContent());
      QueueSleep=Integer.parseInt(eGlobal.getElementsByTagName("queuesleep").item(0).getTextContent());
      //Fin iLee configuracion    

      ReadQueue = new LinkedBlockingQueue<String>(QueueLength);
      InsertQueue = new LinkedBlockingQueue<Register>(QueueLength);

      Reader cReader = new Reader(mtcr, nlReader, ReadQueue, QueueSleep, fileorg);
      Proc cProc = new Proc(mtcp, nlProc, ReadQueue, InsertQueue, QueueSleep);
      SolrInsert cSolrInsert = new SolrInsert(mtci, nlInsert, InsertQueue, QueueSleep, FixValues);

      cReader.start();
      cProc.start();
      cSolrInsert.start();

      while ((mtcr.FinProc()==false || mtcp.FinProc()==false || mtci.FinProc()==false) && error==false) {
        try {
          Thread.sleep(200);
        } catch(InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
        if (mtcr.Error()){
          error=true;
          errmsg=mtcr.getErrorDesc();
          mtcp.Parar();
          mtci.Parar();
        }
        if (mtcp.Error()){
          error=true;
          errmsg=mtcp.getErrorDesc();
          mtcr.Parar();
          mtci.Parar();
        }
        if (mtci.Error()){
          error=true;
          errmsg=mtci.getErrorDesc();
          mtcp.Parar();
          mtcr.Parar();
        }
      }
    }
    catch (Exception e) {
      error=true;
      String errStack=null;
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      errStack=sw.toString();
      errmsg=errStack.split("\\n+")[0] + errStack.split("\\n+")[1];
    }

    if (error) {throw new Exception(errmsg);}
  }

  public int getReadCount()
  {
    return mtcr.getCount();
  }

  public int getProcCount()
  {
    return mtcp.getCount();
  }

  public int getInsertCount()
  {
    return mtci.getCount();
  }
}



