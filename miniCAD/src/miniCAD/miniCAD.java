package miniCAD;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class miniCAD extends JFrame{
    static ArrayList<MyLine> myLines = new ArrayList<>();// 存放直线的容器
    static ArrayList<MyRectangle> myRectangles = new ArrayList<>();// 存放矩形的容器
    static ArrayList<MyCircle> myCircles = new ArrayList<>();// 存放圆形的容器
    static ArrayList<MyString> myStrings = new ArrayList<>(); // 存放文字的容器
    static Point startPoint = new Point();// 起始点
    Point endPoint = new Point();// 结束点
    private static MyCanvas canvas = new MyCanvas();// 画布
    private MyMenu myMenu = new MyMenu(); // 左面板
    private static float potion; //缩放时的比例

    // 定义一个shape的状态
    private static int state = 0;
    public static final int INIT = 0;
    public static final int SEMI = 1;

    // 定义画哪种图形
    private static int choose = 0;
    public static final int NONE = 0;
    public static final int LINE = 1;
    public static final int RECTANGLE = 2;
    public static final int CIRCLE = 3;
    public static final int STRING = 4;

    // 定义是否拖动图形
    private static int onShape = 0;
    private static int ARR = 0;
    public static final int NO = 0;
    public static final int YES = 1;
    static int order = -1;//被拖动的图形序号

    IO io = new IO();

    public static void main(String[] args){
        miniCAD cad = new miniCAD(); // 建立整个程序
        int a = 1;
    }

    public miniCAD(){ // 描述整个程序
        this.setLayout(null);
        this.setSize(800,600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.add(myMenu); // 添加左侧面板，面板上包括按钮
        this.add(canvas);
        this.setResizable(false);
        this.setTitle("miniCAD");

        // 设置画布
        canvas.setBackground(Color.white);
        Graphics g = canvas.getGraphics();
        canvas.paint(g);


    }


    //内部类，定义一个点
    private static class Point {
        public int x;
        public int y;

        public Point() {
            x = 0;
            y = 0;
        }
    }

    //内部类，定义画布，重写paint方法画出容器中所有shape
    private static class MyCanvas extends Canvas{
        public MyCanvas() {
//            this.setSize(800,600);
            this.setBounds(100,0,700,600);


            // 添加鼠标点击事件
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
//                    System.out.println(state);
                    if(state==INIT){
                        //创建图形
                        if(choose==LINE){
                            MyLine newline = new MyLine();
                            newline.x1 = e.getX();
                            newline.y1 = e.getY();
                            newline.x2 = newline.x1;
                            newline.y2 = newline.y1;
                            myLines.add(newline);
                            state = SEMI;
                        }
                        else if(choose==RECTANGLE){
                            MyRectangle rectangle = new MyRectangle();
                            rectangle.x = e.getX();
                            rectangle.y = e.getY();
                            rectangle.width = 0;
                            rectangle.height = 0;
                            myRectangles.add(rectangle);
                            state=SEMI;
                        }
                        else if(choose==CIRCLE){
                            MyCircle circle = new MyCircle();
                            circle.x = e.getX();
                            circle.y = e.getY();
                            circle.width = 0;
                            circle.height = 0;
                            myCircles.add(circle);
                            state=SEMI;
                        }

                        //选中图形
                        else if(choose==NONE){
                            for(MyLine item : myLines){
                                if(item.isOnLine(e.getX(),e.getY())){
                                    order = myLines.indexOf(item);
                                    onShape = YES;
                                    ARR = LINE;
                                    startPoint.x = e.getX();
                                    startPoint.y = e.getY();
                                    potion = (float) (item.y1-item.y2)/(float)(item.x1-item.x2);
                                    break;
//                                    System.out.println(startPoint.x+"  "+startPoint.y);
                                }
                            }
                            if(onShape==NO){
                                for(MyRectangle item:myRectangles){
                                    if(item.isOnLine(e.getX(),e.getY())){
                                        order = myRectangles.indexOf(item);
                                        onShape=YES;
                                        ARR=RECTANGLE;
                                        startPoint.x = e.getX();
                                        startPoint.y = e.getY();
                                        potion = (float)(item.width)/(float)(item.height);
                                        System.out.println("potion="+potion);
                                        break;
                                    }
                                }
                            }
                            if(onShape==NO){
                                for(MyCircle item:myCircles){
                                    if(item.isOnLine(e.getX(),e.getY())){
                                        order = myCircles.indexOf(item);
                                        onShape=YES;
                                        ARR = CIRCLE;
                                        startPoint.x = e.getX();
                                        startPoint.y = e.getY();
                                        potion = (float)(item.width)/(float)(item.height);
                                        break;
                                    }
                                }
                            }
                            if(onShape==NO){
                                for(MyString item:myStrings){
                                    if(item.isOnLine(e.getX(),e.getY())){
                                        order = myStrings.indexOf(item);
                                        onShape=YES;
                                        ARR=STRING;
                                        startPoint.x=e.getX();
                                        startPoint.y=e.getY();
                                        break;
                                    }
                                }
                            }
                        }

                    }
                    //第二个点
                    else if(state==SEMI){
                        if(choose==LINE){
                            int size = myLines.size();
//                            System.out.println(size);
                            myLines.get(size-1).x2 = e.getX();
                            myLines.get(size-1).y2 = e.getY();
                            canvas.repaint();
                            state = INIT;
                            choose = NONE;
                        }
                        else if(choose==RECTANGLE){
                            int size = myRectangles.size();
                            myRectangles.get(size-1).width = e.getX()-myRectangles.get(size-1).x;
                            myRectangles.get(size-1).height = e.getY()-myRectangles.get(size-1).y;
                            canvas.repaint();
                            state = INIT;
                            choose = NONE;
                        }
                        else if(choose==CIRCLE){
                            int size = myCircles.size();
                            myCircles.get(size-1).width = e.getX()-myCircles.get(size-1).x;
                            myCircles.get(size-1).height = e.getY()-myCircles.get(size-1).y;
                            canvas.repaint();
                            state = INIT;
                            choose = NONE;
                        }
                    }

                }

                // 拖动图形
                @Override
                public void mousePressed(MouseEvent e) {
                    for(MyLine item : myLines){
                        if(item.isOnLine(e.getX(),e.getY())){
                            order = myLines.indexOf(item);
                            onShape = YES;
                            ARR = LINE;
                            startPoint.x = e.getX();
                            startPoint.y = e.getY();
                            System.out.println(startPoint.x+"  "+startPoint.y);
                        }
                    }
                    for(MyRectangle rectangle:myRectangles){
                        if(rectangle.isOnLine(e.getX(),e.getY())){
                            order = myRectangles.indexOf(rectangle);
                            onShape = YES;
                            ARR = RECTANGLE;
                            startPoint.x = e.getX();
                            startPoint.y = e.getY();

                        }
                    }
                    for(MyCircle circle:myCircles){
                        if(circle.isOnLine(e.getX(),e.getY())){
                            order = myCircles.indexOf(circle);
                            onShape = YES;
                            ARR = CIRCLE;
                            startPoint.x = e.getX();
                            startPoint.y = e.getY();
                        }
                    }
                    for(MyString string:myStrings){
                        if(string.isOnLine(e.getX(),e.getY())){
                            order = myStrings.indexOf(string);
                            onShape=YES;
                            ARR = STRING;
                            startPoint.x = e.getX();
                            startPoint.y = e.getY();
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    order=-1;
                    onShape = NO;
                    ARR = 0;
                }

            });


            //添加鼠标滑动事件
            this.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    super.mouseMoved(e);
                    if(state==SEMI){
                        if(choose==LINE){
                            int size = myLines.size();
                            MyLine temp = myLines.get(size-1);
                            temp.x2 = e.getX();
                            temp.y2 = e.getY();
                            myLines.set(size-1,temp);
                            canvas.repaint();
                        }
                        else if(choose==RECTANGLE){
                            int size = myRectangles.size();
                            MyRectangle rectangle = myRectangles.get(size-1);
                            rectangle.width = e.getX()-rectangle.x;
                            rectangle.height = e.getY()-rectangle.y;
                            myRectangles.set(size-1,rectangle);
                            canvas.repaint();
                        }
                        else if(choose==CIRCLE){
                            int size = myCircles.size();
                            MyCircle circle = myCircles.get(size-1);
                            circle.width = e.getX()-circle.x;
                            circle.height = e.getY()-circle.y;
                            myCircles.set(size-1,circle);
                            canvas.repaint();
                        }

                    }

                }

                // 拖动图形
                @Override
                public void mouseDragged(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.x1+=e.getX()-startPoint.x;
                            line.x2+=e.getX()-startPoint.x;
                            line.y1+=e.getY()-startPoint.y;
                            line.y2+=e.getY()-startPoint.y;
                            startPoint.x+=e.getX()-startPoint.x;
                            startPoint.y+=e.getY()-startPoint.y;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.x+=e.getX()-startPoint.x;
                            rectangle.y+=e.getY()-startPoint.y;
                            startPoint.x+=e.getX()-startPoint.x;
                            startPoint.y+=e.getY()-startPoint.y;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.x+=e.getX()-startPoint.x;
                            circle.y+=e.getY()-startPoint.y;
                            startPoint.x+=e.getX()-startPoint.x;
                            startPoint.y+=e.getY()-startPoint.y;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR == STRING){
                            MyString string = myStrings.get(order);
                            string.x+=e.getX()-startPoint.x;
                            string.y+=e.getY()-startPoint.y;
                            startPoint.x+=e.getX()-startPoint.x;
                            startPoint.y+=e.getY()-startPoint.y;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }


            });

            //添加鼠标滚轮事件
            this.addMouseWheelListener(new MouseAdapter() {
                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
//                    System.out.println("wheel");
                    if(onShape==YES){
                        // 向上滚，放大
                        if(e.getWheelRotation()==-1){
                            if(ARR==LINE){
                                MyLine line = myLines.get(order);
                                line.stroke+=0.1;
                                myLines.set(order,line);
                                canvas.repaint();
                            }
                            else if(ARR==RECTANGLE){
                                MyRectangle rectangle = myRectangles.get(order);
                                rectangle.stroke+=0.1;
                                myRectangles.set(order,rectangle);
                                canvas.repaint();
                            }
                            else if(ARR==CIRCLE){
                                MyCircle circle = myCircles.get(order);
                                circle.stroke+=0.1;
                                myCircles.set(order,circle);
                                canvas.repaint();
                            }
                            else if(ARR==STRING){
                                MyString string = myStrings.get(order);
                                string.stroke+=0.1;
                                myStrings.set(order,string);
                                canvas.repaint();
                            }
                        }
                        // 向下滚，缩小
                        else if(e.getWheelRotation()==1){
                            if(ARR==LINE){
                                MyLine line = myLines.get(order);
                                line.stroke-=0.1;
                                myLines.set(order,line);
                                canvas.repaint();
                            }
                            else if(ARR==RECTANGLE){
                                MyRectangle rectangle = myRectangles.get(order);
                                rectangle.stroke-=0.1;
                                myRectangles.set(order,rectangle);
                                canvas.repaint();
                            }
                            else if(ARR==CIRCLE){
                                MyCircle circle = myCircles.get(order);
                                circle.stroke-=0.1;
                                myCircles.set(order,circle);
                                canvas.repaint();
                            }
                        }

                    }
                }
            });

            // 按键事件
            this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    // 退格键删除图形
                    if(e.getKeyChar()=='\b'){
                        if(onShape==YES){
                            if(ARR==LINE){
                                myLines.remove(order);
                                canvas.repaint();
                            }
                            else if(ARR==RECTANGLE){
                                myRectangles.remove(order);
                                canvas.repaint();
                            }
                            else if(ARR==CIRCLE){
                                myCircles.remove(order);
                                canvas.repaint();
                            }
                            else if(ARR==STRING){
                                myStrings.remove(order);
                                canvas.repaint();
                            }
                        }
                    }

                    // w键放大
                    if(e.getKeyChar()=='w'){
                        if(onShape==YES){//选中图形
                            if(ARR==LINE){ // 缩放直线
                                MyLine line = myLines.get(order);
//                                float potion = (float) (line.y1-line.y2)/(float)(line.x1-line.x2);
                                if(line.x1<line.x2 && line.y1<line.y2){
                                    line.x1-=3;
                                    line.y1-=(float)(3*potion);
                                    line.x2+=3;
                                    line.y2+=(float)(3*potion);

                                }
                                else if(line.x1>line.x2 && line.y1<line.y2){
                                    line.x1+=3;
                                    line.y1+=(float)(3*potion);
                                    line.x2-=3;
                                    line.y2-=(float)(3*potion);
                                }
                                else if(line.x1>line.x2 && line.y1>line.y2){
                                    line.x1+=3;
                                    line.y1+=(float)(3*potion);
                                    line.x2-=3;
                                    line.y2-=(float)(3*potion);
                                }
                                else{
                                    line.x1-=3;
                                    line.y1-=(float)(3*potion);
                                    line.x2+=3;
                                    line.y2+=(float)(3*potion);
                                }
                                myLines.set(order,line);
                                canvas.repaint();
                            }
                            else if(ARR==RECTANGLE){
                                MyRectangle rectangle = myRectangles.get(order);
//                                float potion = (float)(rectangle.width)/(float)(rectangle.height);
                                rectangle.x-=3*potion;
                                rectangle.y-=3;
                                rectangle.width+=6*potion;
                                rectangle.height+=6;
                                myRectangles.set(order,rectangle);
                                canvas.repaint();
                            }
                            else if(ARR==CIRCLE){
                                MyCircle circle = myCircles.get(order);
                                circle.x-=3*potion;
                                circle.y-=3;
                                circle.width+=6*potion;
                                circle.height+=6;
                                myCircles.set(order,circle);
                                canvas.repaint();
                            }
                            else if(ARR==STRING){
                                MyString string = myStrings.get(order);
                                string.size+=3;
                                string.addjustFont();
                                myStrings.set(order,string);
                                canvas.repaint();
                            }
                        }
                    }
                    // s键缩小
                    else if(e.getKeyChar()=='s'){
                        if(onShape==YES){//选中图形
                            if(ARR==LINE){ // 缩放直线
                                MyLine line = myLines.get(order);
//                                float potion = (float) (line.y1-line.y2)/(float)(line.x1-line.x2);
                                if(line.x1<line.x2 && line.y1<line.y2){
                                    line.x1+=3;
                                    line.y1+=3*potion;
                                    line.x2-=3;
                                    line.y2-=3*potion;

                                }
                                else if(line.x1>line.x2 && line.y1<line.y2){
                                    line.x1-=3;
                                    line.y1-=3*potion;
                                    line.x2+=3;
                                    line.y2+=3*potion;
                                }
                                else if(line.x1>line.x2 && line.y1>line.y2){
                                    line.x1-=3;
                                    line.y1-=3*potion;
                                    line.x2+=3;
                                    line.y2+=3*potion;
                                }
                                else{
                                    line.x1+=3;
                                    line.y1+=3*potion;
                                    line.x2-=3;
                                    line.y2-=3*potion;
                                }
                                myLines.set(order,line);
                                canvas.repaint();
                            }
                            else if(ARR==RECTANGLE){
                                MyRectangle rectangle = myRectangles.get(order);
//                                float potion = (float)(rectangle.width)/(float)(rectangle.height);
                                rectangle.x+=3*potion;
                                rectangle.y+=3;
                                rectangle.width-=6*potion;
                                rectangle.height-=6;
                                myRectangles.set(order,rectangle);
                                canvas.repaint();
                            }
                            else if(ARR==CIRCLE){
                                MyCircle circle = myCircles.get(order);
                                circle.x+=3*potion;
                                circle.y+=3;
                                circle.width-=6*potion;
                                circle.height-=6;
                                myCircles.set(order,circle);
                                canvas.repaint();
                            }
                            else if(ARR==STRING){
                                MyString string = myStrings.get(order);
                                string.size-=3;
                                string.addjustFont();
                                myStrings.set(order,string);
                                canvas.repaint();
                            }
                        }
                    }
                }
            });
        }
        @Override
        public void paint(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            for(MyLine myLine : myLines){
                g.setColor(myLine.c);
                g2.setStroke(new BasicStroke(myLine.stroke));//粗细
                System.out.println("draw:read x1:"+myLine.x1+"read y1:"+myLine.y1);
                g.drawLine(myLine.x1,myLine.y1,myLine.x2,myLine.y2);
            }
            for(MyRectangle rectangle:myRectangles){
                g.setColor(rectangle.c);
                g2.setStroke(new BasicStroke((rectangle.stroke)));
                g.drawRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
            }
            for(MyCircle circle:myCircles){
                g.setColor(circle.c);
                g2.setStroke(new BasicStroke(circle.stroke));
                g.drawOval(circle.x,circle.y,circle.width,circle.height);
            }
            for(MyString string:myStrings){
                g.setColor(string.c);
                g2.setStroke(new BasicStroke(string.stroke));
                g.setFont(string.font);
                g.drawString(string.string,string.x,string.y);
            }

        }

        //重写update方法，解决闪屏问题
        @Override
        public void update(Graphics g) {
            Image offScreenImage = this.createImage(700,600);
            Graphics gImage = offScreenImage.getGraphics();
            paint(gImage);
            g.drawImage(offScreenImage,0,0,null);
        }


    }

    // 左侧面板
    private class MyMenu extends JPanel {
        JButton lineButton = new JButton("直线");
        JButton rectangleButton = new JButton("矩形");
        JButton circleButton = new JButton("圆形");
        JButton stringButton = new JButton("文字");
        JButton saveButton = new JButton("保存");
        JButton openButton = new JButton("打开");
        JButton introButton = new JButton("说明");
        JPanel Yellow = new JPanel();
        JPanel Green = new JPanel();
        JPanel Orange = new JPanel();
        JPanel Red = new JPanel();
        JPanel Pink = new JPanel();
        JPanel Magenta = new JPanel();
        JPanel Black = new JPanel();
        JPanel Gray = new JPanel();
        JPanel Blue = new JPanel();
        private String input;
        Font font = new Font("黑体",Font.BOLD,25);

        public MyMenu() throws HeadlessException {
            this.setLayout(null);
//            this.setSize(100,600);
            this.setBounds(0,0,100,600);
            lineButton.setBounds(10,10,80,40);
            this.add(lineButton);
            rectangleButton.setBounds(10,60,80,40);
            this.add(rectangleButton);
            circleButton.setBounds(10,110,80,40);
            this.add(circleButton);
            stringButton.setBounds(10,160,80,40);
            this.add(stringButton);
            introButton.setBounds(10,210,80,40);
            this.add(introButton);

            saveButton.setBounds(10,500,80,40);
            this.add(saveButton);
            openButton.setBounds(10,450,80,40);
            this.add(openButton);

            Yellow.setLayout(null);
            Yellow.setBackground(Color.YELLOW);
            Yellow.setBounds(5,350,30,30);
            this.add(Yellow);
            Green.setLayout(null);
            Green.setBackground(Color.green);
            Green.setBounds(35,350,30,30);
            this.add(Green);
            Orange.setLayout(null);
            Orange.setBackground(Color.orange);
            Orange.setBounds(65,350,30,30);
            this.add(Orange);
            Red.setLayout(null);
            Red.setBackground(Color.red);
            Red.setBounds(5,380,30,30);
            this.add(Red);
            Pink.setLayout(null);
            Pink.setBackground(Color.pink);
            Pink.setBounds(35,380,30,30);
            this.add(Pink);
            Magenta.setLayout(null);
            Magenta.setBackground(Color.magenta);
            Magenta.setBounds(65,380,30,30);
            this.add(Magenta);
            Black.setLayout(null);
            Black.setBackground(Color.black);
            Black.setBounds(5,410,30,30);
            this.add(Black);
            Gray.setLayout(null);
            Gray.setBackground(Color.gray);
            Gray.setBounds(35,410,30,30);
            this.add(Gray);
            Blue.setLayout(null);
            Blue.setBackground(Color.blue);
            Blue.setBounds(65,410,30,30);
            this.add(Blue);


            lineButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    choose = LINE;
                }
            });
            rectangleButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    choose=RECTANGLE;
                }
            });
            circleButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    choose = CIRCLE;
                }
            });
            stringButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    choose = STRING;
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    JFrame newframe = new JFrame("输入文字");
                    JTextField text = new JTextField();
                    JButton okButton = new JButton("确定");
                    JButton cancelButton = new JButton("取消");

                    newframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    newframe.setSize(400,200);
                    newframe.setLocation(screenSize.width/2-200, screenSize.height/2-100);
                    newframe.setVisible(true);
                    newframe.setResizable(false);
                    newframe.setLayout(null);

                    okButton.setBounds(120,120,70,30);
                    cancelButton.setBounds(210,120,70,30);
                    newframe.add(okButton);
                    newframe.add(cancelButton);

                    text.setBounds(30,30,330,50);
                    text.setFont(font);
                    newframe.add(text);

                    okButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            input = text.getText();
                            System.out.println(input);
                            MyString string = new MyString();
                            string.string = input;
                            myStrings.add(string);
                            canvas.repaint();
                            choose=NONE;
                            newframe.dispose();
                        }
                    });

                    cancelButton.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            choose=NONE;
                            newframe.dispose();
                        }
                    });
                }
            });
            introButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    String text = "<html>  " +
                            "<body>" +
                            "<p>鼠标左键：确定起始点</p>" +
                            "<p>鼠标左键：选中图形</p>" +
                            "<p>鼠标滚轮向上：加粗</p>" +
                            "<p>鼠标滚轮向下：变细</p>" +
                            "<p>退格键：删除图形</p>" +
                            "<p>w：放大</p>" +
                            "<p>s：缩小</p>" +
                            "</body>" +
                            "</html>";
                    String disText="<html><HTML><body style=color:red>Tooltip in <br> Multiline</body></html>";

                    JFrame frame = new JFrame("说明");
                    JLabel label = new JLabel();
                    label.setText(text);

                    frame.setResizable(false);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setLocation(screenSize.width/2-200, screenSize.height/2-100);
                    frame.setSize(400,200);
                    frame.setVisible(true);
                    frame.add(label);
                }
            });

            // 打开文件
            openButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        io.read();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            });

            // 保存文件
            saveButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        io.write();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }
            });

            //各种颜色按钮
            Yellow.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.c=Color.yellow;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.c = Color.yellow;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.c = Color.yellow;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR==STRING){
                            MyString string = myStrings.get(order);
                            string.c = Color.yellow;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }
            });
            Green.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.c=Color.green;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.c = Color.green;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.c = Color.green;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR==STRING){
                            MyString string = myStrings.get(order);
                            string.c = Color.green;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }
            });
            Orange.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.c=Color.orange;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.c = Color.orange;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.c = Color.orange;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR==STRING){
                            MyString string = myStrings.get(order);
                            string.c = Color.orange;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }
            });
            Red.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.c=Color.red;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.c = Color.red;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.c = Color.red;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR==STRING){
                            MyString string = myStrings.get(order);
                            string.c = Color.red;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }
            });
            Pink.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.c=Color.pink;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.c = Color.pink;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.c = Color.pink;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR==STRING){
                            MyString string = myStrings.get(order);
                            string.c = Color.pink;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }
            });
            Magenta.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.c=Color.magenta;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.c = Color.magenta;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.c = Color.magenta;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR==STRING){
                            MyString string = myStrings.get(order);
                            string.c = Color.magenta;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }
            });
            Black.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.c=Color.black;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.c = Color.black;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.c = Color.black;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR==STRING){
                            MyString string = myStrings.get(order);
                            string.c = Color.black;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }
            });
            Gray.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.c=Color.gray;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.c = Color.gray;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.c = Color.gray;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR==STRING){
                            MyString string = myStrings.get(order);
                            string.c = Color.gray;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }
            });
            Blue.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(onShape==YES){
                        if(ARR==LINE){
                            MyLine line = myLines.get(order);
                            line.c=Color.blue;
                            myLines.set(order,line);
                            canvas.repaint();
                        }
                        else if(ARR==RECTANGLE){
                            MyRectangle rectangle = myRectangles.get(order);
                            rectangle.c = Color.blue;
                            myRectangles.set(order,rectangle);
                            canvas.repaint();
                        }
                        else if(ARR==CIRCLE){
                            MyCircle circle = myCircles.get(order);
                            circle.c = Color.blue;
                            myCircles.set(order,circle);
                            canvas.repaint();
                        }
                        else if(ARR==STRING){
                            MyString string = myStrings.get(order);
                            string.c = Color.blue;
                            myStrings.set(order,string);
                            canvas.repaint();
                        }
                    }
                }
            });
        }
    }

    // 直线类
    static class MyLine implements Serializable{
        public int x1;
        public int y1;
        public int x2;
        public int y2;
        public float stroke;
        public Color c;

        public MyLine() {
            stroke = 3.0f;
            c = Color.black;
            x1=0;
            x2=0;
            y1=0;
            y2=0;
        }

        // 判断鼠标指针是否在线上
        public boolean isOnLine(int x, int y){
            /*
            float a = (float)(x-x1)/(float)(x2-x);
            float b = (float)(y-y1)/(float)(y2-y);
            float a2 = (float)(x2-x)/(float)(x-x1);
            float b2 = (float)(y2-y)/(float)(y-y1);
            if(a-b<=0.1 && b-a<=0.1)
            return true;
            else if(a2-b2<=0.1 && b2-a2<=0.1) return true;
            else return false;
            */
            int a = (this.y2-this.y1)*x + (this.x1-this.x2)*y + (this.y1-this.y2)*this.x1 + (this.x2-this.x1)*this.y1;
            if(a<0) a = -a;
            a=a*a;

            int b = (this.y2-this.y1)*(this.y2-this.y1) + (this.x1-this.x2)*(this.x1-this.x2);

            float res = (float)a/(float)b;

//            System.out.println(res);

            if((x>this.x1-3 && x<this.x2+3) ||(x<this.x1+3 && x>this.x2-3)){
                if((y>this.y1-3 && y<this.y2+3) ||(y<this.y1+3 && x>this.y2-3)){
                    if(res<10) return true;
                }
            }
            return false;
        }


    }

    // 矩形类
     static class MyRectangle implements Serializable{
        public Color c;
        public float stroke;
        public int x;
        public int y;
        public int width;
        public int height;

        public MyRectangle(){
            c=Color.black;
            stroke=3.0f;
        }
        public boolean isOnLine(int x, int y){
            if((x>=this.x-2 && x<=this.x+2) || (x>=this.x+this.width-2 && x<=this.x+this.width+2)){
                if(y>=this.y && y<=this.y+this.height)
                    return true;
            }
            else if((y>=this.y-2 && y<=this.y+2) || (y>=this.y+this.height-2 && y<=this.y+this.height+2)){
                if(x>=this.x && x<=this.x+this.width)
                    return true;
            }
            else
                return false;
            return false;
        }
    }

    // 圆形类
     static class MyCircle implements Serializable{
        public Color c;
        public float stroke;
        public int x;
        public int y;
        public int width;
        public int height;

        public MyCircle(){
            c=Color.black;
            stroke=3.0f;
        }

        // 判断点在椭圆上
        public boolean isOnLine(int x, int y){
            int centerx, centery;
            centerx = this.x + this.width/2;
            centery = this.y + this.height/2;

            float value = (float)(x-centerx)*(float)(x-centerx)/(float)this.width/(float)this.width
                    +(float)(y-centery)*(float)(y-centery)/(float)this.height/(float)this.height;
            value*=4.0;
            if(value >=0.9 && value <= 1.1) return true;
            else return false;
        }
    }

    // 文字类
     static class MyString implements Serializable{
        Color c;
        float stroke;
        String string;
        int x;
        int y;
        Font font; // 默认字体
        int size;

        public MyString(){
            size=50;
            font = new Font("黑体",Font.BOLD,size);
            x=100;
            y=100;
            c=Color.black;
            stroke=3.0f;
        }

        public void addjustFont(){
            font = new Font("黑体",Font.BOLD,size);
        }

        public boolean isOnLine(int x, int y){
            int length = string.length();
            if(x>=this.x && x <= this.x+length*this.size){
                if(y>=this.y-size && y<=this.y){
                    return true;
                }
            }
            return false;
        }

    }

    // 文件输入输出类
     static class IO implements Serializable{
        public void write() throws IOException {
            String path;
            ObjectOutputStream oos = null;

            JFrame saveFrame = new JFrame();
            FileDialog save = new FileDialog(saveFrame, "保存文件", FileDialog.SAVE);
            save.setVisible(true);
            path = save.getDirectory()+save.getFile();
            System.out.println(path);

            try {
                oos = new ObjectOutputStream(new FileOutputStream(path));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // 输出line
            int size = myLines.size();
            oos.writeObject(size);
            for(MyLine line : myLines){
                try {
                    oos.writeObject(line);
                    oos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            // 输出rectangle
            size = myRectangles.size();
            oos.writeObject(size);
            for(MyRectangle rectangle : myRectangles){
                try {
                    oos.writeObject(rectangle);
                    oos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            // 输出circle
            size = myCircles.size();
            oos.writeObject(size);
            for(MyCircle circle : myCircles){
                try {
                    oos.writeObject(circle);
                    oos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            //输出string
            size = myStrings.size();
            oos.writeObject(size);
            for(MyString string : myStrings){
                try {
                    oos.writeObject(string);
                    oos.flush();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            try {
                oos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        public void read() throws IOException, ClassNotFoundException {
            String path;
            ObjectInputStream ois = null;

            JFrame openFrame = new JFrame();
            FileDialog open = new FileDialog(openFrame, "打开文件", FileDialog.LOAD);
            open.setVisible(true);
            path = open.getDirectory() + open.getFile();

            try {
                ois = new ObjectInputStream(new FileInputStream(path));
            } catch (IOException e1) {
                e1.printStackTrace();
            }


            // 输入line
            int size;
            size = (int)ois.readObject();
            System.out.println(size);

            myLines = new ArrayList<>();

            for(int i=0;i<size;i++){
                MyLine line = null;
                line = (MyLine)ois.readObject();
                System.out.println("read x1:"+line.x1+"read y1:"+line.y1);
                state=INIT;
                choose = NONE;
                onShape=NO;
                ARR = NONE;
                myLines.add(line);
                System.out.println("in arraylist, read x1:"+myLines.get(i).x1+"read y1:"+myLines.get(i).y1);
            }

            //输入rectangle
            size = (int)ois.readObject();
            System.out.println(size);

            myRectangles = new ArrayList<>();

            for(int i=0;i<size;i++){
                MyRectangle rectangle = null;
                rectangle = (MyRectangle) ois.readObject();
                state=INIT;
                choose = NONE;
                onShape=NO;
                ARR = NONE;
                myRectangles.add(rectangle);
            }

            //输入circle
            size = (int)ois.readObject();
            System.out.println(size);

            myCircles = new ArrayList<>();

            for(int i=0;i<size;i++){
                MyCircle circle = null;
                circle = (MyCircle) ois.readObject();
                state=INIT;
                choose = NONE;
                onShape=NO;
                ARR = NONE;
                myCircles.add(circle);
            }

            //输入string
            size = (int)ois.readObject();
            System.out.println(size);

            myStrings = new ArrayList<>();

            for(int i=0;i<size;i++){
                MyString string = null;
                string = (MyString) ois.readObject();
                state=INIT;
                choose = NONE;
                onShape=NO;
                ARR = NONE;
                myStrings.add(string);
            }

            canvas.repaint();

        }
    }

}


