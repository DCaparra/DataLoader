package org.dataloader;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;


public class Reader extends Thread
{
  private File fileorg=null;
  private boolean fileeof=false;
  private MTCom mtc=null;
  private BlockingQueue<String> ReadQueue=null;
  private int QueueSleep=50;

  public Reader(MTCom mtc, NodeList nlReader, BlockingQueue bq, int QueueSleep, File fileorg) throws Exception
  {
    this.mtc=mtc;
    this.ReadQueue=bq;
    this.fileorg=fileorg;
    this.QueueSleep=QueueSleep;

    //Lee configuracion del XML
    Node nReader = nlReader.item(0);
    Element eReader = (Element) nReader;
  }

  public void run()
  {
    try
    {
      String line = null;

      //Lee el archivo de origen
      BufferedReader in = new BufferedReader (new FileReader(fileorg));
      line = in.readLine();

      while(line != null && mtc.Continuar())
      {
        if(ReadQueue.offer(line, QueueSleep, TimeUnit.MILLISECONDS))
        {
          line = in.readLine();
          mtc.setCountMasUno();
        }
      }
      fileeof=true;
      in.close();
    }
    catch (IOException e){
      mtc.setError(e);
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

