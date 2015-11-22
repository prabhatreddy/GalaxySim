import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

class params{
    
    public static int t = 0;

}
class Star{
  private static final double G = 6.673e-11;       
  private static final double solarmass=1.98892e30;
    
  public double rx, ry;      
  public double vx, vy;       
  public double fx, fy;      
  public double mass;        
  public String img;         
  
  public Star(double rx, double ry, double vx, double vy, double mass, String img) {  
    this.rx    = rx;
    this.ry    = ry;
    this.vx    = vx;
    this.vy    = vy;
    this.mass  = mass;
    this.img = "star.gif";
  }

  public void update(double dt) {    
    vx += dt * fx / mass;
    vy += dt * fy / mass;
    rx += dt * vx;
    ry += dt * vy;
  }
  
  public double distanceTo(Star s) {
    double dx = rx - s.rx;
    double dy = ry - s.ry;
    return Math.sqrt(dx*dx + dy*dy);
  }
  
  public void resetForce() {
    fx = 0.0;
    fy = 0.0;
  }
  
  public void addForce(Star s) {
    Star a = this;
    double EPS = 3E4;
    double dx = s.rx - a.rx;
    double dy = s.ry - a.ry;
    double dist = Math.sqrt(dx*dx + dy*dy);
    double F = (G * a.mass * s.mass) / (dist*dist + EPS*EPS);
    a.fx += F * dx / dist;
    a.fy += F * dy / dist;
  }
  
  public String toString() {
    return "" + rx + ", "+ ry+ ", "+  vx+ ", "+ vy+ ", "+ mass+ " "+img;
  }
}

class StarTest{
    public int N = 100;
    public Star stars[]= new Star[1000000];
    
  public static double circlev(double rx, double ry) {
    double solarmass=1.98892e30;
    double rx2 = rx*rx;
    double ry2 = ry*ry;
    double r2=Math.sqrt(rx2+ry2);
    double numerator=(6.67e-11)*1e6*solarmass;
    return Math.sqrt(numerator/r2);
  }
  
  public void startthebodies1(int N) {
    double radius = 1e18;
    double solarmass=1.98892e30;
    for (int i = 0; i < N; i++) {
      double px = 1e17*exp(-1.8)*(.5-Math.random());
      double py = 1e17*exp(-1.8)*(.5-Math.random());
      double magv = circlev(px,py);
      
      double absangle = Math.atan(Math.abs(py/px));
      double thetav= Math.PI/2-absangle;
      double phiv = Math.random()*Math.PI;
      double vx   = -1*Math.signum(py)*Math.cos(thetav)*magv;
      double vy   = Math.signum(px)*Math.sin(thetav)*magv;
           if (Math.random() <=.5) {
              vx=-vx;
              vy=-vy;
            } 
      double mass = Math.random()*solarmass*10+1e20;
      stars[i]   = new Star(px, py, vx, vy, mass, "star.gif");
        //System.out.println(stars[i].toString());
      if(i>2*N/3){
        stars[i]   = new Star(px+4e17, py+4e17, vx, vy, mass, "star.gif");
      }
    }
    stars[0]= new Star(0,0,0,0,1e6*solarmass,"star2.gif");
    stars[2*N/3] = new Star(4e17,4e17,0,0,1e6*solarmass,"star2.gif");
  }
  
  public void combine(int n){
      for(int i = 0; i < n;i++){
          for(int j = 0; j < n; j++){
             if(i!=j && stars[i] != null && stars[j] != null){
                double irx = Math.abs(stars[i].rx);
                double jrx = Math.abs(stars[j].rx);
                double iry = Math.abs(stars[i].ry);
                double jry = Math.abs(stars[j].ry);
              if(i == 0 && j == 2*n/3){
                  if(Math.abs(irx - jrx) <= 2e16 && Math.abs(iry - jry) <= 2e16){
                  double m = stars[i].mass + stars[j].mass;
                  stars[i].vx = ((stars[i].mass*stars[i].vx) + (stars[j].mass*stars[j].vx))/m;
                  stars[i].vy = ((stars[i].mass*stars[i].vy) + (stars[j].mass*stars[j].vy))/m;
                  stars[i].mass = m;
                  stars[j] = null;
              }
              }
              if(Math.abs(irx - jrx) <= 5e13 && Math.abs(iry - jry) <= 5e13){
                  double m = stars[i].mass + stars[j].mass;
                  stars[i].vx = ((stars[i].mass*stars[i].vx) + (stars[j].mass*stars[j].vx))/m;
                  stars[i].vy = ((stars[i].mass*stars[i].vy) + (stars[j].mass*stars[j].vy))/m;
                  stars[i].mass = m;
                  stars[j] = null;
              }
           
          }
      }
      }
  }
  
  public void addforces(int N) {
    for (int i = 0; i < N; i++) {
      if(stars[i]!= null) stars[i].resetForce();
      for (int j = 0; j < N; j++) {
        if (i != j && stars[i] != null && stars[j] != null) {
              stars[i].addForce(stars[j]);
          }
      }
    }
    for (int i = 0; i < N; i++) {
      if(stars[i] != null) stars[i].update(1e11);
    }    
  }
   public static double exp(double lambda) {
        return -Math.log(1 - Math.random()) / lambda;
    }
   
   public void captureScreen(String fileName) throws Exception {
 
   Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
   Rectangle screenRectangle = new Rectangle(screenSize);
   Robot robot = new Robot();
   BufferedImage image = robot.createScreenCapture(screenRectangle);
   ImageIO.write(image, "png", new File(fileName));
 
}
   
   
  }

class Main{
    public static void main(String[] args) throws Exception {
        StarTest st = new StarTest();
        int n = 2001;
        st.startthebodies1(n);
        int t = 0;
        while(t < 100000){
        for(int i = 0; i <n; i++){
            if(st.stars[i]==null || st.stars[i].vx >= 9e4 || st.stars[i].vy >= 9e4) System.out.println("didn't draw" + i);
                else if(i == 0 || i == 2*n/3 && st.stars[i].mass != 0){
                    Zen.drawImage("star2.gif", (int) (200+(st.stars[i].rx/(2*Math.pow(10,15)))), (int) (200+(st.stars[i].ry/(2*Math.pow(10,15)))), 4, 4);
                }
                else if(i<2*n/3 && st.stars[i].mass != 0){
                    Zen.drawImage("star.gif", (int) (200+(st.stars[i].rx/(2*Math.pow(10,15)))), (int) (200+(st.stars[i].ry/(2*Math.pow(10,15)))), 2, 2);
                }
                else if(st.stars[i].mass != 0){
                    Zen.drawImage("star1.gif", (int) (200+(st.stars[i].rx/(2*Math.pow(10,15)))), (int) (200+(st.stars[i].ry/(2*Math.pow(10,15)))), 2, 2);
                }
            }
        st.addforces(n);
        t++;
        if(true){
            st.combine(n);
        }
        st.captureScreen("stars" + t + ".png");
        Zen.flipBuffer();
      }
    }
}
