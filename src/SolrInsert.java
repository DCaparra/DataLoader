package org.dataloader;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public class SolrInsert extends Thread
{  
  private NodeList nlInsert=null;
  private BlockingQueue<Register> InsertQueue=null;
  private int QueueSleep=50;
  private MTCom mtc=null;
  private HashMap<String, SolrClient> cores=null;
  private HashMap<String, String> FixValues=null;

  public SolrInsert(MTCom mtc, NodeList nlInsert, BlockingQueue bq, int QueueSleep, HashMap<String, String> FixValues)  throws Exception
  {
    this.mtc=mtc;
    this.nlInsert=nlInsert;
    this.InsertQueue = bq;
    this.QueueSleep=QueueSleep;
    this.FixValues=FixValues;

    cores=new HashMap<String, SolrClient>();

    Node nInsert = nlInsert.item(0);
    Element eInsert = (Element) nInsert;

    NodeList nlUrl =  eInsert.getElementsByTagName("url");
    for (int i = 0; i < nlUrl.getLength(); i++) {
      Node nUrl = nlUrl.item(i);

      if (nUrl.getNodeType() == Node.ELEMENT_NODE) {
        Element eUrl = (Element) nUrl;
        SolrClient solr = new HttpSolrClient.Builder(eUrl.getTextContent()).build();
        cores.put(eUrl.getAttribute("id"), solr);
      }
    }
  }
     
  public void run()
  {
    Register reg=null;

    try
    {
      while (mtc.Continuar() && (InsertQueue.size()>0 || mtc.FinProd()==false))
      {
        reg=InsertQueue.poll(QueueSleep, TimeUnit.MILLISECONDS);
        SolrClient solr = cores.get(reg.GetDesign());
        SolrInputDocument document = new SolrInputDocument();

        for(Map.Entry<String, String> entry : FixValues.entrySet()) {
          document.addField(entry.getKey(), entry.getValue());
        }

        HashMap<String, String> m = reg.GetItems();
        for(Map.Entry<String, String> entry : m.entrySet()) {
          document.addField(entry.getKey(), entry.getValue());
        }

	UpdateResponse response = solr.add(document);
        mtc.setCountMasUno();
      }
      for(Map.Entry<String, SolrClient> entry : cores.entrySet()) {
        SolrClient solr=entry.getValue();
        solr.commit();
      }
    }
    catch (InterruptedException e){
      mtc.setError(e);
    }
    catch (Exception e){
      mtc.setError(e);
    }
    mtc.setFinProc(true);
  }
}

