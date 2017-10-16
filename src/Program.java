import java.awt.Font;
import java.util.HashMap;

import studios.vanish.engine.Color;
import studios.vanish.engine.FillMode;
import studios.vanish.engine.GraphicsUnit;
import studios.vanish.engine.Index;
import studios.vanish.engine.Light;
import studios.vanish.engine.Object3D;
import studios.vanish.engine.Point;
import studios.vanish.engine.Size;
import studios.vanish.engine.Vertex;
import studios.vanish.engine.Window;

public class Program
{
    /**
     * Internal Structures
     */
    enum State
    {
        Game, Menu, Intro
    }
    /**
     * Window Variables
     */
    // Size
    static Size                             SIZE = new Size(800, 600);
    static Size                             RESOLUTION = new Size(1920, 1200);
    // String
    static String                           NAME = "PIXE| RUN";
    // boolean
    static boolean                          BORDER = false;
    static boolean                          INTERSECTION = false;
    static boolean                          AUTOMATIC_RENDER = false;
    static boolean                          RENDER_LIGHTS = true;
    static boolean                          CAMERA_FREE_FLOATING = false;
    static boolean                          FULLSCREEN = true;
    // int
    static int                              FPS = 200;
    static int                              FOV = 1024;
    static int                              HUMAN_DETAIL_FACE = 20;
    static int                              HUMAN_DETAIL_BODY = 3;
    static int                              HUMAN_DETAIL_EYE = 5;
    // Color
    static Color                            COLOR_GUN_STRIP = Color.DarkSlateGray;
    static Color                            COLOR_GUN_FORM = Color.Black;
    static Color                            COLOR_GUN_FACE = Color.Black;
    static Color                            COLOR_GUN_EFACE = Color.Red;
    static Color                            COLOR_HUMAN_FACE = Color.White;
    static Color                            COLOR_HUMAN_EYE = Color.Black;
    static Color                            COLOR_HUMAN_BODY = Color.Black;
    // FillMode
    static FillMode                         FILLMODE = FillMode.Solid;
    // Audio
    static Audio                            SOUND_INTRO = new Audio("sounds/intro.wav", 0, 0);
    // HashMap
    static HashMap<String, String>          EVENTS = new HashMap<String, String>();
    static HashMap<String, String>          KEYS = new HashMap<String, String>();
    // Font
    static Font                             FONT_BUTTON = new Font("Century Gothic", Font.PLAIN, 30);
    static Font                             FONT_TITLE = new Font("Bradley Hand ITC", Font.PLAIN, 125);
    static Font                             FONT_TITLE_SMALL = new Font("Bradley Hand ITC", Font.PLAIN, 75);
    static Font                             FONT_HUD = new Font("Monospaced", Font.PLAIN, 40);
    static Font                             FONT_HUD_SMALL = new Font("Monospaced", Font.PLAIN, 20);
    static Font                             FONT_STAT = new Font("Courier New", Font.PLAIN, 15);
    /**
     * Program Variables
     */
    Window wnd = new Window(NAME, SIZE, BORDER);
    State state = State.Intro;
    int menuState = 0;
    int introTime = 0;
    Object3D[] introObjects;
    Light[] introLights;

    Vertex[] gunVertices = new Vertex[]
    {
        new Vertex(-0.75, +0.75, -3.00), new Vertex(+0.75, +0.75, -3.00), new Vertex(+0.75, -0.75, -3.00), new Vertex(-0.75, -0.75, -3.00),
        new Vertex(-0.75, +0.75, +3.00), new Vertex(+0.75, +0.75, +3.00), new Vertex(+0.75, -0.75, +3.00), new Vertex(-0.75, -0.75, +3.00),
        new Vertex(-0.75, -4.00, -2.75), new Vertex(+0.75, -4.00, -2.75), new Vertex(+0.75, -4.00, -3.75), new Vertex(-0.75, -4.00, -3.75),
        new Vertex(-0.75, -0.75, -2.00), new Vertex(+0.75, -0.75, -2.00), new Vertex(+0.75, -0.75, -3.00), new Vertex(-0.75, -0.75, -3.00),
    };
    Index[] gunIndices = new Index[]
    {
        new Index( 0,  1,  2,  3),
        new Index( 4,  5,  6,  7),
        new Index( 0,  1,  5,  4),
        new Index( 2,  3,  7,  6),
        new Index( 1,  2,  6,  5),
        new Index( 0,  3,  7,  4),
        new Index( 8,  9, 10, 11),
        new Index(12, 13, 14, 15),
        new Index( 8,  9, 13, 12),
        new Index(10, 11, 15, 14),
        new Index( 9, 10, 14, 13),
        new Index( 8, 11, 15, 12)
    };
    Color[] gunColors = new Color[]
    {
        Program.COLOR_GUN_EFACE,
        Program.COLOR_GUN_EFACE,
        Program.COLOR_GUN_STRIP,
        Program.COLOR_GUN_STRIP,
        Program.COLOR_GUN_FORM,
        Program.COLOR_GUN_FORM,
        Program.COLOR_GUN_STRIP,
        Program.COLOR_GUN_FORM,
        Program.COLOR_GUN_STRIP,
        Program.COLOR_GUN_STRIP,
        Program.COLOR_GUN_FORM,
        Program.COLOR_GUN_FORM
    };

    public static void main(String[] args)
    {
        // Pre Game
        InitializeEvents();
        // Start
        new Program();
    }
    public static void InitializeEvents()
    {
        CSV eventFile = new CSV("events/window.csv");
        for (int i = 0; i < eventFile.Size.Height; i++)
        {
            EVENTS.put(eventFile.Get(0, i), eventFile.Get(1, i));
        }
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
        wnd.OnPaint.Add(this, EVENTS.get("OnPaint"));
        wnd.OnResize.Add(this, EVENTS.get("OnResize"));
        wnd.Initialize(FPS);
        wnd.Initialize3D(RESOLUTION, FOV, FILLMODE, INTERSECTION, AUTOMATIC_RENDER);
        wnd.Show();
        wnd.RenderLights = RENDER_LIGHTS;
        wnd.Camera.FreeFloating = CAMERA_FREE_FLOATING;
        if (FULLSCREEN)
        {
            wnd.ToFullScreen();
        }
        Light.Ambient = Color.Black;
    }
    public void Update()
    {
        if (state == State.Intro)
        {
            if (introTime == 0)
            {
                wnd.Camera.Location = new Vertex(0, 0, -3);
                introObjects = new Object3D[3];
                introObjects[0] = new Person();
                introObjects[0].Location = new Vertex(0, 0, 2);

                introObjects[1] = new Object3D(gunVertices, gunIndices, gunColors);
                //introObjects[2] = new Object3D(gunVertices, gunIndices, gunColors);

                introLights = new Light[1];
                introLights[0] = new Light(new Vertex(0, 0, 0), new Point(0.5, 0.5), Color.Black);

                SOUND_INTRO.Play();
            }
            introTime++;
            if (introTime < 2550)
            {
                introObjects[0].Location.Z -= 0.002;
                introObjects[0].Location.Y -= 0.0006;
                introObjects[0].Rotation.Z -= 0.09;
            }
            else if (introTime == 2550)
            {
                introLights[0].Attenuation = new Point(20, 20);
                introObjects[1].Location = new Vertex(-4, 0, 10);
                introObjects[1].Rotation = new Vertex(45, 0, -45);
            }
            else if (introTime < 5500)
            {
                introObjects[1].Rotation.Z += 0.1;
                introObjects[1].Location.Z -= 0.001;
            }
            else if (introTime < 8500)
            {

            }
            else if (introTime == 8500)
            {
                
            }
        }
        else
        {
            introTime = 0;
        }
    }
    public void Input()
    {

    }
    public void Render_Clear(GraphicsUnit Graphics, Color Background)
    {
        Graphics.FillRectangle(Background, new Point(0, 0), wnd.Size);
    }
    public void Render_2D(GraphicsUnit Graphics)
    {
        if (state == State.Intro)
        {
            if (introTime < 2550)
            {
                int alpha = 255 - (introTime / 10);
                Graphics.FillRectangle(new Color(0, 0, 0, alpha), new Point(0, 0), wnd.Size);
                Graphics.DrawString("" + alpha, Color.White, new Point(0, 20), FONT_STAT);
            }
            else if (introTime < 5100)
            {
                int alpha = 255 - ((introTime / 10) - 255);
                Graphics.FillRectangle(new Color(0, 0, 0, alpha), new Point(0, 0), wnd.Size);
                Graphics.DrawString("" + alpha, Color.White, new Point(0, 20), FONT_STAT);
            }
            else if (introTime < 7650)
            {
                int alpha = ((introTime / 10) - 510);
                Graphics.DrawString("A Vanish Studios Production", new Color(100, 100, 100, alpha), new Point((wnd.Size.Width / 2) - (Graphics.GetTextSize("A Vanish Studios Production", FONT_TITLE_SMALL).Width / 2), wnd.Size.Height - 200), FONT_TITLE_SMALL);
                Graphics.DrawString("" + alpha, Color.White, new Point(0, 20), FONT_STAT);
            }
            else if (introTime < 8500)
            {
                Graphics.DrawString("A Vanish Studios Production", new Color(100, 100, 100, 255), new Point((wnd.Size.Width / 2) - (Graphics.GetTextSize("A Vanish Studios Production", FONT_TITLE_SMALL).Width / 2), wnd.Size.Height - 200), FONT_TITLE_SMALL);
            }
            Graphics.DrawString("" + introTime, Color.White, new Point(0, 0), FONT_STAT);
        }
    }
    public void Render_3D(GraphicsUnit Graphics)
    {
        if (state == State.Intro)
        {
            if (introObjects != null)
            {
                for (int i = 0; i < introObjects.length; i++)
                {
                    if (introObjects[i] != null)
                    {
                        introObjects[i].Render(Graphics);
                    }
                }
            }
            if (introLights != null)
            {
                for (int i = 0; i < introLights.length; i++)
                {
                    if (introLights[i] != null)
                    {
                        introLights[i].Render(Graphics);
                    }
                }
            }
        }
    }
    public void Render(GraphicsUnit Graphics)
    {
        Render_Clear(Graphics, Color.Black);
        Render_3D(Graphics);
        Graphics.Render();
        Render_2D(Graphics);
    }
}