package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;


public class GUI extends JFrame implements MouseListener {
    //original x,y  current x,y   radius
    int ox,oy,cx,cy,r;


    static Queue<Node> q=new LinkedList<Node>();
    Vector<Integer> tx=new Vector<Integer>();
    Vector<Integer> ty=new Vector<Integer>();
    boolean toggle=false;

    GUI(){
        property();
        mouseAction();
        JB();
        p2();
    }

//    set GUI property
    private void property(){
        this.setSize(960,800);
        this.setLocationRelativeTo(null);
        this.setTitle("NEOCIS Program");
        this.getContentPane().setBackground(Color.gray);
        this.setLayout(null);
        this.setVisible(true);
    }

    //draw 20*20 point grid
    public void p1(Graphics g,Color c){

        int x=80; int y=80; final int w=10; final int h=10;
        g.setColor(c);
        for (int i=x;i<=867;i+=40){
            for (int j=y;j<=672;j+=30){
                g.fillRect(i,j,w,h);
            }
        }
        this.setVisible(true);

    }
    // push every point to Node structure
    public void p2(){

        int x=80; int y=80; final int w=10; final int h=10;
        for (int i=x;i<=867;i+=40){

            for (int j=y;j<=672;j+=30){
                for (int k=0;k<10;k++){
                    tx.add(i+k);
                    ty.add(j+k);
                }
                q.offer(new Node(i,j,tx,ty));
                tx.clear();
                ty.clear();
            }
        }

        this.setVisible(true);

    }

//    get mouse position
    void mouseAction(){
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ox=e.getX();
                oy=e.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                cx=e.getX(); cy=e.getY();
                r= (int) Math.sqrt(Math.pow(Math.abs(ox-cx),2)+Math.pow(Math.abs(oy-cy),2));
                System.out.println(r);
                System.out.println("Mouse pressed position：");
                System.out.println("X coordinate："+ox);
                System.out.println("Y coordinate："+oy);
                System.out.println("Mouse Released position：");
                System.out.println("X coordinate："+cx);
                System.out.println("Y coordinate："+cy);
                if (toggle==true){
                    mdc(getGraphics());
                    HighP(getGraphics(),ox,oy,r);
                }
            }
        });
    }

    //Toggle off
    void To(Graphics g){
        g.setColor(Color.gray);
        g.fillRect(9,32,944,780);
    }

    //mouse draw Circle
    void mdc(Graphics g){
        int diameter = r * 2;
        g.setColor(Color.blue);
        g.drawOval(ox-r,oy-r,diameter, diameter);
    }

    // part2 button to toggle point
    void JB(){

        JButton button=new JButton("Generate");
        button.setBounds(400,680,120,40);
        add(button);
        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                super.mouseClicked(e);
                if (toggle ==false) {
                    p1(getGraphics(), Color.black);
                } else {
                    p1(getGraphics(), Color.gray );
                }

                if ((toggle == false)) {
                    toggle = true;
                } else {
                    toggle = false;
                    To(getGraphics());
                }
            }
        });
    }

    //find highlight point   Graphics g,int x,int y,int r
    void HighP(Graphics g,int x,int y,int r){

        int u;
        int ct=0;
        int maxu=Integer.MIN_VALUE;
        int minu=Integer.MAX_VALUE;
        //four peek of a point
        Vector<Vector<Integer>> pe= new Vector<Vector<Integer>>(4);
        Vector<Integer> cur=new Vector<Integer>();
        Queue<Node> tmp=new LinkedList<Node>(q);

        while(!tmp.isEmpty()) {

            cur.add(tmp.peek().vx.firstElement());
            cur.add(tmp.peek().vy.firstElement());
            pe.add((Vector<Integer>) cur.clone());
            cur.clear();

            cur.add(tmp.peek().vx.firstElement());
            cur.add(tmp.peek().vy.lastElement());
            pe.add((Vector<Integer>) cur.clone());
            cur.clear();

            cur.add(tmp.peek().vx.lastElement());
            cur.add(tmp.peek().vy.firstElement());
            pe.add((Vector<Integer>) cur.clone());
            cur.clear();

            cur.add(tmp.peek().vx.lastElement());
            cur.add(tmp.peek().vy.lastElement());
            pe.add((Vector<Integer>) cur.clone());
            cur.clear();

            //calculate distance between peeks and center of circle
            for (Vector k : pe) {
                int tx = (int) k.firstElement();
                int ty = (int) k.lastElement();
                u = (int) Math.sqrt(Math.pow(Math.abs(ox - tx), 2) + Math.pow(Math.abs(oy - ty), 2));
                if (u < r) {
                    ct++;
                }

            }

            //highlight point
            if (ct > 0&&ct<4) {
                int dx, dy;
                dx = pe.firstElement().get(0);
                dy = pe.firstElement().get(1);
                g.setColor(Color.blue);
                g.fillRect(dx, dy, 10, 10);
                System.out.println("Highlight point:"+dx+" "+dy);

                for (int k=0;k<pe.capacity();k++) {
                    dx=pe.get(k).firstElement();
                    dy=pe.get(k).lastElement();
                    maxu = Math.max(maxu,(int) Math.sqrt(Math.pow(Math.abs(ox - dx), 2) + Math.pow(Math.abs(oy - dy), 2)));
                    minu = Math.min(minu,(int) Math.sqrt(Math.pow(Math.abs(ox - dx), 2) + Math.pow(Math.abs(oy - dy), 2)));
                }
            }
            tmp.poll();
            pe.clear();
            ct=0;
        }

        //draw largest and smallest radius of the highlighted points
        if (maxu==Integer.MIN_VALUE||minu==Integer.MAX_VALUE){
            System.out.println("No Highlight Point!");
        }else {
            System.out.println("Largest radius:" + maxu+"\n"+"Blue circle radius:"+r+"\n"+ "Smallest radius" + minu);
        }
        g.setColor(Color.black);
        g.drawOval(ox-maxu,oy-maxu,maxu*2, maxu*2);
        g.drawOval(ox-minu,oy-minu,minu*2, minu*2);

    }



    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }




}

// create data structure for every 20*20 point
class Node{
    int x;
    int y;
    Vector<Integer> vx=new Vector<Integer>();
    Vector<Integer> vy=new Vector<Integer>();
    Node(){}
    Node(int x,int y,Vector<Integer> vx,Vector<Integer> vy){
        this.x=x;
        this.y=y;
        this.vx=(Vector<Integer>) vx.clone();;
        this.vy=(Vector<Integer>) vy.clone();;
    }
}


