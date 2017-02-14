package org.dataloader;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import java.lang.InterruptedException;
import java.util.ArrayList;
import java.util.Iterator;

public class Proc extends Thread
{
  private MTCom mtc=null;

  private BlockingQueue<String> ReadQueue=null;
  private BlockingQueue<Register> InsertQueue=null;

  private int EstReg=0;
  private String RegDelim=null;
  private int QueueSleep=50;
  private int ProcControl=0;

  private ArrayList<Design> Designs=new ArrayList<Design> ();

  public Proc(MTCom mtc, NodeList nlProc, BlockingQueue<String> bqr, BlockingQueue<Register> bqi, int QueueSleep) throws Exception
  {
    this.mtc=mtc;
    this.ReadQueue=bqr;
    this.InsertQueue=bqi;
    this.QueueSleep=QueueSleep;

    //Lee parametros XML
    Node nProc = nlProc.item(0);
    Element eProc = (Element) nProc;

    EstReg=Integer.parseInt(eProc.getElementsByTagName("EstReg").item(0).getTextContent());
    RegDelim=eProc.getElementsByTagName("RegDelim").item(0).getTextContent();
    ProcControl=Integer.parseInt(eProc.getElementsByTagName("ProcControl").item(0).getTextContent());

    NodeList nlDesign =  eProc.getElementsByTagName("design");
    for (int p = 0; p < nlDesign.getLength(); p++) {
      Node nDesign = nlDesign.item(p);

      if (nDesign.getNodeType() == Node.ELEMENT_NODE) {
        Element eDesign = (Element) nDesign;

        Design d=new Design(eDesign, EstReg, RegDelim);
        Designs.add(d);
      }
    }
    //Fin lee parametros
  }
     
  public void run()
  {
    int RegNum=0;
    int IdentCount=0;
    Register reg=null;
    String line=null;
    try
    {
      //Consumidor-Productor
      while (mtc.Continuar() && (ReadQueue.size()>0 || mtc.FinProd()==false)) {
        IdentCount=0;
        line=ReadQueue.poll(QueueSleep, TimeUnit.MILLISECONDS);

        if (line != null) {
          RegNum++;
          mtc.setCountMasUno();
          for (Design d : Designs) {
            d.SetLine(line, RegNum, (ReadQueue.size()==0 && mtc.FinProd()==true));
            if (d.Identificado()==true)
            {
              IdentCount++;
              reg=d.GetRegister();
              while(InsertQueue.offer(reg, QueueSleep, TimeUnit.MILLISECONDS) == false && mtc.Continuar()){}
            }
          }
        }
        if (ProcControl == 2) {
          if (IdentCount > 1) {throw new Exception("Error, se identifico mas de un diseño para el reguistro numero: " + RegNum);}
          if (IdentCount < 1) {throw new Exception("Error, no se pudo identificar diseño para el reguistro numero: " + RegNum);}
        } else if (ProcControl == 1) {
          if (IdentCount < 1) {throw new Exception("Error, no se pudo identificar diseño para el reguistro numero: " + RegNum);}
        }
      }
      //Fin proceso Consumidor-Productor
    }
    catch (Exception e){
      mtc.setError(e);
    }
    mtc.setFinProc(true);
  }
}
