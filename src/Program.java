import studios.vanish.engine.Window;
import studios.vanish.engine.Size;
import studios.vanish.engine.Vertex;
import studios.vanish.engine.Point;
import studios.vanish.engine.Rectangle;
import studios.vanish.engine.FillMode;
import studios.vanish.engine.Camera;
import studios.vanish.engine.Color;
import studios.vanish.engine.GraphicsUnit;
import studios.vanish.engine.Light;
import studios.vanish.engine.Button;
import studios.vanish.utility.Utility;
import java.awt.Font;
import java.awt.event.KeyEvent;
public class Program
{
    /**
     * Default Variables
     */
    static Size                 SIZE = new Size(800, 600);
    static Size                 RESOLUTION = new Size(1920, 1200);
    
    static String               NAME = "Slender: Pixelized Maze";
    
    static boolean              BORDER = false;
    static boolean              INTERSECTION = false;
    static boolean              AUTOMATICRENDER = false;
    static boolean              CAMERA_FREE_FLOATING = false;
    static boolean              RENDER_LIGHTS = true;
    static boolean              FULLSCREEN = true;
    static boolean              STATS = true;
    
    static CSV[]                LEVEL_CSV = new CSV[]
    {
        new CSV("level01.csv"),
        new CSV("level02.csv"),
        new CSV("level03.csv")
    };
    
    static int                  DEFAULT_LEVEL = 1;
    static int                  FOV = 1024;
    static int                  FPS = 200;
    static int                  LIGHT_DISTANCE = 25;
    static int                  CLIP_DISTANCE = 15;
    static int                  HUMAN_DETAIL_FACE = 20;
    static int                  HUMAN_DETAIL_BODY = 3;
    static int                  HUMAN_DETAIL_EYE = 5;  
    static int                  SLENDER_WAIT = 500;
    static int                  PAGES_MAX = 9;
    static int                  HEALTH_MAX = 100;

    static Color                COLOR_GUN_STRIP = Color.DarkSlateGray;
    static Color                COLOR_GUN_FORM = Color.Black;
    static Color                COLOR_GUN_FACE = Color.Black;
    static Color                COLOR_GUN_EFACE = Color.Red;
    static Color                COLOR_HUMAN_FACE = Color.White;
    static Color                COLOR_HUMAN_EYE = Color.Black;
    static Color                COLOR_HUMAN_BODY = Color.Black;
    
    static Audio                SOUND_MENU = new Audio("SOUND_MENU.wav", 0, 0);
    static Audio                SOUND_GAME = new Audio("SG.wav", 0, 0);

    static FillMode             FILLMODE = FillMode.Solid;    

    static Font                 FONT_BUTTON = new Font("Impact", Font.PLAIN, 30);
    static Font                 FONT_TITLE = new Font("Chiller", Font.PLAIN, 100);
    static Font                 FONT_HUD = new Font("Monospaced", Font.PLAIN, 40);    
    static Font                 FONT_HUD_SMALL = new Font("Monospaced", Font.PLAIN, 20);    
    
    Window wnd = new Window(NAME, SIZE, BORDER);
    Light[] flashLight;
    Maze maze;
    Rectangle player;
    Rectangle enemy;
    int gameMode = 1;
    int level = DEFAULT_LEVEL;
    Button[][] Buttons;
    Person slender = new Person();
    int slenderWait = 0;
    int coins = 0;
    int health = HEALTH_MAX;
    
    public static void main(String[] args)
    {
        new Program();
    }
    public Program()
    {
        Initialize(FPS);
        while (true)
        {
            Update();
            Input();
            wnd.Wait(2);
        }
    }
    public void Initialize(int FPS)
    {
        wnd.OnPaint.Add(this, "Render");
        wnd.Initialize(FPS);
        wnd.Initialize3D(RESOLUTION, FOV, FILLMODE, INTERSECTION, AUTOMATICRENDER);
        wnd.Show();
        wnd.RenderLights = RENDER_LIGHTS;
        wnd.Camera.FreeFloating = CAMERA_FREE_FLOATING;
        if (FULLSCREEN)
        {
            wnd.ToFullScreen();
        }
        Light.Ambient = Color.Black;
        flashLight = new Light[LIGHT_DISTANCE];
        for (int i = 0; i < flashLight.length; i++)
        {
            flashLight[i] = new Light(new Vertex(0, 0, 0), new Point(0.15 + (0.01 * i / 10), 0.15 + (0.01 * i)), Color.Black);
        }
        SetMaze(level);
        SetFlashLight();
        SOUND_MENU.Loop = true;
        SOUND_MENU.Play();
        SOUND_GAME.Loop = true;
        InitializeButtons();
    }
    public void InitializeButtons()
    {
        Buttons = new Button[1][0];
        Buttons[0] = new Button[5];
        Buttons[0][0] = new Button(wnd, "Play", new Point(50, wnd.Size.toPoint().Y - 100 - 50), new Size(150, 100));
        Buttons[0][0].TextFont = FONT_BUTTON;
        Buttons[0][0].SetBackColor(new Color(Color.Black, 0), new Color(Color.Black, 50), new Color(Color.Black, 100));
        Buttons[0][0].SetForeColor(new Color(Color.Orange, 255), new Color(Color.Orange, 50), new Color(Color.Orange, 100));
        Buttons[0][0].OnClick.Add(this, "Event_Button_Play");
        
        Buttons[0][1] = new Button(wnd, "Levels", new Point(50 + (160 * 1), wnd.Size.toPoint().Y - 100 - 50), new Size(150, 100));
        Buttons[0][1].TextFont = FONT_BUTTON;
        Buttons[0][1].SetBackColor(new Color(Color.Black, 0), new Color(Color.Black, 50), new Color(Color.Black, 100));
        Buttons[0][1].SetForeColor(new Color(Color.White, 255), new Color(Color.White, 50), new Color(Color.White, 100));

        Buttons[0][2] = new Button(wnd, "Controls", new Point(50 + (160 * 2), wnd.Size.toPoint().Y - 100 - 50), new Size(150, 100));
        Buttons[0][2].TextFont = FONT_BUTTON;
        Buttons[0][2].SetBackColor(new Color(Color.Black, 0), new Color(Color.Black, 50), new Color(Color.Black, 100));
        Buttons[0][2].SetForeColor(new Color(Color.White, 255), new Color(Color.White, 50), new Color(Color.White, 100));

        Buttons[0][3] = new Button(wnd, "About", new Point(50 + (160 * 3), wnd.Size.toPoint().Y - 100 - 50), new Size(150, 100));
        Buttons[0][3].TextFont = FONT_BUTTON;
        Buttons[0][3].SetBackColor(new Color(Color.Black, 0), new Color(Color.Black, 50), new Color(Color.Black, 100));
        Buttons[0][3].SetForeColor(new Color(Color.White, 255), new Color(Color.White, 50), new Color(Color.White, 100));

        Buttons[0][4] = new Button(wnd, "Exit", new Point(wnd.Size.Width - 200, wnd.Size.Height - 100 - 50), new Size(150, 100));
        Buttons[0][4].TextFont = FONT_BUTTON;
        Buttons[0][4].SetBackColor(new Color(Color.Black, 0), new Color(Color.Black, 50), new Color(Color.Black, 100));
        Buttons[0][4].SetForeColor(new Color(Color.Red, 255), new Color(Color.White, 50), new Color(Color.White, 100));
        Buttons[0][4].OnClick.Add(this, "Event_Button_Exit");
    }
    public void SetMaze(int index)
    {
        maze = new Maze(LEVEL_CSV[index], 3);
        Point ps = maze.GetCenterPoint(maze.Start);
        wnd.Camera.Location = new Vertex(ps.X, 1.5, ps.Y);
        slender = new Person();
        Point v = maze.OpenRects.get(Utility.Random(0, maze.OpenRects.size())).Location;
        Point p = maze.GetCenterPoint(v);
        slender.Location = new Vertex(p.X, 0.75, p.Y);
    }
    public void Update()
    {
        if (gameMode == 0)
        {
            slender.Turn(0.01, wnd.Camera.Location);
            wnd.Camera.Look(0.15, wnd.Location, wnd.Size);
            maze.Update();
            for (int i = 0; i < maze.PickUps.size(); i++)
            {
                Vertex a = wnd.Camera.Location;
                Vertex b = maze.PickUps.get(i).Location;
                double mag = Math.sqrt(Math.pow(a.X - b.X, 2) + Math.pow(a.Y - b.Y, 2) + Math.pow(a.Z - b.Z, 2));
                if (mag < 1)
                {
                    maze.PickUpRects.remove(i);
                    maze.PickUps.remove(i);
                    maze.PickUpLights.remove(i);
                    coins++;
                    i--;
                }
            }
            double speed = 0.005;
            Camera c = new Camera(slender.Location, slender.Rotation.multiply(new Vertex(1, 1, -1)));
            if (!Intersects(c.GetForwardPoint(speed), new Size(10, 10)))
            {
                c.Forward(speed);
            }
            else
            {
                Vertex NLoc = c.GetForwardPoint(speed);
                Vertex C1Loc = new Vertex(NLoc);
                C1Loc.X = c.Location.X;
                if (!Intersects(C1Loc, new Size(10, 10)))
                {
                    c.Location.X = C1Loc.X;
                    c.Location.Z = C1Loc.Z;
                }
                Vertex C2Loc = new Vertex(NLoc);
                C2Loc.Z = c.Location.Z;
                if (!Intersects(C2Loc, new Size(10, 10)))
                {
                    c.Location.X = C2Loc.X;
                    c.Location.Z = C2Loc.Z;
                }
            }
            slender.Location = c.Location;
            slender.Walk(speed * 2);
            slenderWait++;
            if (slenderWait > SLENDER_WAIT)
            {
                slenderWait = 0;
                Vertex a = wnd.Camera.Location;
                Vertex b = slender.Location;
                double mag = Math.sqrt(Math.pow(a.X - b.X, 2) + Math.pow(a.Y - b.Y, 2) + Math.pow(a.Z - b.Z, 2));
                while (mag > 20)
                {
                    Point v = maze.OpenRects.get(Utility.Random(0, maze.OpenRects.size() - 1)).Location;
                    Point p = maze.GetCenterPoint(v);
                    b = new Vertex(p.X, 0.75, p.Y);          
                    mag = Math.sqrt(Math.pow(a.X - b.X, 2) + Math.pow(a.Y - b.Y, 2) + Math.pow(a.Z - b.Z, 2));       
                }
                slender.Location = b;
            }
        }
        else
        {
            Buttons[0][0].Location = new Point(50, wnd.Size.toPoint().Y - 100 - 50);            
            Buttons[0][1].Location = new Point(50 + (160 * 1), wnd.Size.toPoint().Y - 100 - 50);
            Buttons[0][2].Location = new Point(50 + (160 * 2), wnd.Size.toPoint().Y - 100 - 50);
            Buttons[0][3].Location = new Point(50 + (160 * 3), wnd.Size.toPoint().Y - 100 - 50);
            Buttons[0][4].Location = new Point(wnd.Size.Width - 200, wnd.Size.Height - 100 - 50);
        }
    }
    public boolean Intersects(Vertex _location, Size _objSize)
    {
        Size s = new Size(15, 15);
        Rectangle obj = new Rectangle(Color.Yellow, maze.GetPointFromLocation(_location).multiply(s.toPoint()).add(new Point(2, 2)), _objSize);
        for (int i = 0; i < maze.BordersRects.size(); i++)
        {
            if (maze.BordersRects.get(i).Intersects(obj))
            {
                return true;
            }
        }
        return false;
    }
    public void Input()
    {
        if (gameMode == 0)
        {
            double speed = 0.01;
            if (wnd.Keys[KeyEvent.VK_SHIFT])
            {
                speed = 0.02;
            }
            if (wnd.Keys[KeyEvent.VK_W])
            {
                if (!wnd.Camera.FreeFloating)
                {
                    if (!Intersects(wnd.Camera.GetForwardPoint(speed), new Size(10, 10)))
                    {
                        wnd.Camera.Forward(speed);
                    }
                    else
                    {
                        Vertex NLoc = wnd.Camera.GetForwardPoint(speed);
                        Vertex C1Loc = new Vertex(NLoc);
                        C1Loc.X = wnd.Camera.Location.X;
                        if (!Intersects(C1Loc, new Size(10, 10)))
                        {
                            wnd.Camera.Location.X = C1Loc.X;
                            wnd.Camera.Location.Z = C1Loc.Z;
                        }
                        Vertex C2Loc = new Vertex(NLoc);
                        C2Loc.Z = wnd.Camera.Location.Z;
                        if (!Intersects(C2Loc, new Size(10, 10)))
                        {
                            wnd.Camera.Location.X = C2Loc.X;
                            wnd.Camera.Location.Z = C2Loc.Z;
                        }
                    }
                }
                else
                {
                    wnd.Camera.Forward(speed);
                }
            }
            if (wnd.Keys[KeyEvent.VK_S])
            {
                if (!wnd.Camera.FreeFloating)
                {
                    if (!Intersects(wnd.Camera.GetForwardPoint(-speed), new Size(10, 10)))
                    {
                        wnd.Camera.Forward(-speed);
                    }
                    else
                    {
                        Vertex NLoc = wnd.Camera.GetForwardPoint(-speed);
                        Vertex C1Loc = new Vertex(NLoc);
                        C1Loc.X = wnd.Camera.Location.X;
                        if (!Intersects(C1Loc, new Size(10, 10)))
                        {
                            wnd.Camera.Location.X = C1Loc.X;
                            wnd.Camera.Location.Z = C1Loc.Z;
                        }
                        Vertex C2Loc = new Vertex(NLoc);
                        C2Loc.Z = wnd.Camera.Location.Z;
                        if (!Intersects(C2Loc, new Size(10, 10)))
                        {
                            wnd.Camera.Location.X = C2Loc.X;
                            wnd.Camera.Location.Z = C2Loc.Z;
                        }
                    }
                }
                else
                {
                    wnd.Camera.Forward(-speed);
                }
            }
            if (wnd.Keys[KeyEvent.VK_D])
            {
                if (!wnd.Camera.FreeFloating)
                {
                    Camera c = new Camera(wnd.Camera.Location, wnd.Camera.Rotation);
                    c.Rotation.Z -= 90;
                    if (!Intersects(c.GetForwardPoint(speed), new Size(10, 10)))
                    {
                        wnd.Camera.Right(speed);
                    }
                    else
                    {
                        Vertex NLoc = c.GetForwardPoint(speed);
                        Vertex C1Loc = new Vertex(NLoc);
                        C1Loc.X = c.Location.X;
                        if (!Intersects(C1Loc, new Size(10, 10)))
                        {
                            wnd.Camera.Location.X = C1Loc.X;
                            wnd.Camera.Location.Z = C1Loc.Z;
                        }
                        Vertex C2Loc = new Vertex(NLoc);
                        C2Loc.Z = c.Location.Z;
                        if (!Intersects(C2Loc, new Size(10, 10)))
                        {
                            wnd.Camera.Location.X = C2Loc.X;
                            wnd.Camera.Location.Z = C2Loc.Z;
                        }
                    }
                }
                else
                {
                    wnd.Camera.Right(speed);
                }
            }
            if (wnd.Keys[KeyEvent.VK_A])
            {
                if (!wnd.Camera.FreeFloating)
                {
                    Camera c = new Camera(wnd.Camera.Location, wnd.Camera.Rotation);
                    c.Rotation.Z += 90;
                    if (!Intersects(c.GetForwardPoint(speed), new Size(10, 10)))
                    {
                        wnd.Camera.Right(-speed);
                    }
                    else
                    {
                        Vertex NLoc = c.GetForwardPoint(speed);
                        Vertex C1Loc = new Vertex(NLoc);
                        C1Loc.X = c.Location.X;
                        if (!Intersects(C1Loc, new Size(10, 10)))
                        {
                            wnd.Camera.Location.X = C1Loc.X;
                            wnd.Camera.Location.Z = C1Loc.Z;
                        }
                        Vertex C2Loc = new Vertex(NLoc);
                        C2Loc.Z = c.Location.Z;
                        if (!Intersects(C2Loc, new Size(10, 10)))
                        {
                            wnd.Camera.Location.X = C2Loc.X;
                            wnd.Camera.Location.Z = C2Loc.Z;
                        }
                    }
                }
                else
                {
                    wnd.Camera.Right(-speed);
                }
            }
            if (wnd.Keys[KeyEvent.VK_ESCAPE])
            {
                SOUND_GAME.Stop();
                SOUND_MENU.Play();
                gameMode = 1;
            }
            SetFlashLight();
        }
    }
    public void SetFlashLight()
    {
        for (int i = 0; i < flashLight.length; i++)
        {
            flashLight[i].Location = wnd.Camera.GetForwardPoint(i / 5.0);
        }
    }
    public void Event_Button_Play(Point MouseLocation, int Button)
    {
        wnd.Camera.SetMousePreviousPosition(MouseLocation);
        gameMode = 0;
        SOUND_MENU.Stop();
        SOUND_GAME.Play();
    }
    public void Event_Button_Exit(Point MouseLocation, int Button)
    {
        wnd.Close();
    }
    public void Sound_Menu_Start()
    {
        
    }
    public void Render_Menu3D(GraphicsUnit Graphics)
    {
        
    }
    public void Render(GraphicsUnit Graphics)
    {       
        Graphics.FillRectangle(Color.Black, new Point(0, 0), wnd.Size);
        slender.Render(Graphics);
        maze.Render(Graphics, wnd.Camera);
        for (int i = 0; i < flashLight.length; i++)
        {
            flashLight[i].Render(Graphics);
        }
        Graphics.Render();
        if (gameMode == 0)
        {
            String str = coins + "";
            int rectSize = 140;
            if (str.length() == 2)
            {
                rectSize = 160;
            }
            else if (str.length() == 3)
            {
                rectSize = 180;
            }
            Graphics.FillRectangle(new Color(Color.Black, 100), new Point(25, wnd.Size.Height - 80), new Size(rectSize, 60));
            Graphics.DrawString("COINS", new Color(Color.Yellow, 100), new Point(50, wnd.Size.Height - 65), FONT_HUD_SMALL);
            Graphics.DrawString(coins + "", Color.White, new Point(120, wnd.Size.Height - 90), FONT_HUD);
            rectSize = 160;
            str = health + "";
            if (str.length() == 2)
            {
                rectSize = 180;
            }
            else if (str.length() == 3)
            {
                rectSize = 200;
            }
            Graphics.FillRectangle(new Color(Color.Black, 100), new Point(wnd.Size.Width - rectSize - 25, wnd.Size.Height - 80), new Size(rectSize, 60));
            Graphics.DrawString("HEALTH", new Color(Color.Yellow, 100), new Point(wnd.Size.Width - Graphics.GetTextSize("HEALTH", FONT_HUD_SMALL).Width - 50, wnd.Size.Height - 65), FONT_HUD_SMALL);
            Graphics.DrawString(health + "", Color.White, new Point(wnd.Size.Width - rectSize, wnd.Size.Height - 90), FONT_HUD);
        }
        if (gameMode > 0)
        {
            Render_Menu3D(Graphics);
            Graphics.FillRectangle(new Color(Color.Black, 200), new Point(0, 0), wnd.Size);
            Graphics.DrawString(NAME, Color.SlateGray, wnd.Size.toPoint().divide(new Point(2, 2)).subtract(Graphics.GetTextSize(NAME, FONT_TITLE).toPoint().divide(new Point(2, 2))).subtract(new Point(0, 100)), FONT_TITLE);
            int idx = gameMode - 1;
            for (int i = 0; i < Buttons[idx].length; i++)
            {
                if (Buttons[idx][i] != null)
                {
                    Buttons[idx][i].Render(Graphics);
                }
            }
        }
        if (STATS)
        {
            Graphics.DrawString(wnd.FPS + "", Color.White, new Point(0, 0), new Font("Courier New", Font.PLAIN, 15));
            Graphics.DrawString(Graphics.D3D_GetVertexCount() + "", Color.White, new Point(0, 20), new Font("Courier New", Font.PLAIN, 15));
        }
    }
}