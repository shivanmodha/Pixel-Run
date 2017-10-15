import studios.vanish.engine.Object3D;
import studios.vanish.engine.Vertex;
import studios.vanish.engine.Index;
import studios.vanish.engine.Light;
import studios.vanish.engine.Color;
import studios.vanish.engine.GraphicsUnit;
import studios.vanish.engine.Grid;
import studios.vanish.engine.Point;
import studios.vanish.engine.Camera;
import studios.vanish.engine.Size;
import studios.vanish.engine.Rectangle;
import studios.vanish.engine.Sphere;
import java.util.ArrayList;
public class Maze extends Object3D
{
    ArrayList<Grid> walls = new ArrayList<Grid>();
    ArrayList<Grid> ground = new ArrayList<Grid>();
    CSV file;
    int def;
    Point Start;
    Point End;
    ArrayList<Rectangle> BordersRects = new ArrayList<Rectangle>();
    ArrayList<Rectangle> PickUpRects = new ArrayList<Rectangle>();
    ArrayList<Object3D> PickUps = new ArrayList<Object3D>();
    ArrayList<Light> PickUpLights = new ArrayList<Light>();
    Rectangle StartRec;
    ArrayList<Rectangle> OpenRects = new ArrayList<Rectangle>();
    Light StartLight;
    Light EndLight;
    Rectangle EndRec;
    int CoinCount = 0;
    public Maze()
    {
        super();
    }
    public Maze(CSV _csv, int definition)
    {
        Initialize(_csv, definition);
    }
    public void Initialize(CSV _csv, int definition)
    {
        file = _csv;
        def = definition;
        int x = _csv.Size.Width;
        int y = _csv.Size.Height;
        x *= definition;
        y *= definition;
        x += 1;
        y += 1;
        Vertices = new Vertex[x * y];
        int vertexIndex = 0;
        for (int j = 0; j < y; j++)
        {
            for (int i = 0; i < x; i++)
            {
                Vertices[vertexIndex] = new Vertex(i - (x / 2), 0, j - (y / 2));
                vertexIndex++;
            }
        }
        Indices = new Index[(x - 1) * (y - 1)];
        ArrayList<Index> idc = new ArrayList<Index>();
        ArrayList<Color> col = new ArrayList<Color>();
        ArrayList<Point> centerS = new ArrayList<Point>();
        ArrayList<Point> centerE = new ArrayList<Point>();
        Colors = new Color[(x - 1) * (y - 1)];
        vertexIndex = 0;
        for (int j = 0; j < y - 1; j++)
        {
            for (int i = 0; i < x - 1; i++)
            {
                String check = _csv.Get(i / definition, j / definition);
                if (!check.equals("B"))
                {
                    int heightOffset = x * j;
                    idc.add(new Index(heightOffset + i, heightOffset + i + 1, heightOffset + x + i + 1, heightOffset + x + i));
                    //col.add(Color.White);
                    if (check.equals("o"))
                    {
                        col.add(Color.White);
                    }
                    else if (check.equals("B"))
                    {
                        col.add(new Color(0, 0, 0, 0));
                    }
                    else if (check.equals("s"))
                    {
                        col.add(Color.Red);
                        centerS.add(new Point(i / definition, j / definition));
                    }
                    else if (check.equals("e"))
                    {
                        col.add(Color.Green);
                        centerE.add(new Point(i / definition, j / definition));
                    }
                    else if (check.equals("c"))
                    {
                        col.add(Color.Orange);
                    }
                    else
                    {
                        col.add(Color.Cyan);
                    }
                    vertexIndex++;
                }
            }
        }
        Start = new Point(0, 0);
        for (int i = 0; i < centerS.size(); i++)
        {
            Start = Start.add(centerS.get(i));
        }
        Start = Start.divide(new Point(centerS.size(), centerS.size()));
        End = new Point(0, 0);
        for (int i = 0; i < centerE.size(); i++)
        {
            End = End.add(centerE.get(i));
        }
        End = End.divide(new Point(centerE.size(), centerE.size()));
        Indices = idc.toArray(new Index[idc.size()]);
        Colors = col.toArray(new Color[col.size()]);
        Color WallColor = Color.DarkSlateBlue;
        for (int j = 0; j < _csv.Size.Height; j++)
        {
            for (int i = 0; i < _csv.Size.Width; i++)
            {
                if (_csv.Get(i, j).equals("B"))
                {
                    if (IsWalkable(i + 1, j))
                    {
                        Grid g = new Grid(2, definition, WallColor);
                        g.Location = new Vertex((i - (_csv.Size.Width / 2.0) + 1) * definition, 1, ((j - (_csv.Size.Height / 2.0)) * definition) + (definition / 2) + 1);
                        g.Rotation = new Vertex(0, 90, 0);
                        walls.add(g);
                    }
                    if (IsWalkable(i - 1, j))
                    {
                        Grid g = new Grid(2, definition, WallColor);
                        g.Location = new Vertex((i - (_csv.Size.Width / 2.0)) * definition, 1, ((j - (_csv.Size.Height / 2.0)) * definition) + (definition / 2) + 1);
                        g.Rotation = new Vertex(0, 90, 0);
                        walls.add(g);                        
                    }
                    if (IsWalkable(i, j - 1))
                    {
                        Grid g = new Grid(2, definition, WallColor);
                        g.Location = new Vertex((i - (_csv.Size.Width / 2.0)) * definition + 2, 1, ((j - (_csv.Size.Height / 2.0)) * definition));
                        g.Rotation = new Vertex(90, 90, 0);
                        walls.add(g);                        
                    }
                    if (IsWalkable(i, j + 1))
                    {
                        Grid g = new Grid(2, definition, WallColor);
                        g.Location = new Vertex((i - (_csv.Size.Width / 2.0)) * definition + 2, 1, ((j - (_csv.Size.Height / 2.0) + 1) * definition));
                        g.Rotation = new Vertex(90, 90, 0);
                        walls.add(g);                        
                    }
                }
                else
                {
                    String check = _csv.Get(i, j);
                    Color c;
                    int cells = definition * 2;
                    double scale = 0.5;
                    if (check.equals("o"))
                    {
                        c = Color.White;
                    }
                    else if (check.equals("B"))
                    {
                        c = new Color(0, 0, 0, 0);
                    }
                    else if (check.equals("s"))
                    {
                        c = Color.Red;
                        cells = definition * 4;
                        scale = 0.25;
                    }
                    else if (check.equals("e"))
                    {
                        c = Color.Green;
                        cells = definition * 4;
                        scale = 0.25;
                    }
                    else if (check.equals("c"))
                    {
                        c = Color.White;
                    }
                    else
                    {
                        c = Color.Cyan;
                    }
                    Grid g = new Grid(cells, cells, c);
                    g.Scale = new Vertex(scale, scale, scale);
                    g.Location = new Vertex((i * definition) - (_csv.Size.Width * definition / 2) + 1.5, 0, (j * definition) - (_csv.Size.Height * definition / 2) + 1.5);
                    ground.add(g);
                }
            }
        }
        BordersRects = GetBorders();

        Point ps = GetCenterPoint(Start);
        Point pe = GetCenterPoint(End);
        StartLight = new Light(new Vertex(ps.X, 1.5, ps.Y), new Point(1, 0.75), Color.Red);
        EndLight = new Light(new Vertex(pe.X, 1.5, pe.Y), new Point(1, 0.75), Color.Green);
    }
    private boolean IsWalkable(int x, int y)
    {
        String check = file.Get(x, y);
        return (check.equals("o") || check.equals("s") || check.equals("e") || check.equals("c"));
    }
    public void Update()
    {
        for (int i = 0; i < PickUps.size(); i++)
        {
            PickUps.get(i).Rotation.Z += 0.1;
        }        
    }
    public void Render(GraphicsUnit Graphics, Camera Camera)
    {
        //super.Render(Graphics);
        Vertex a = Camera.Location;
        for (int i = 0; i < walls.size(); i++)
        {
            Vertex b = walls.get(i).Location;
            double mag = Math.sqrt(Math.pow(a.X - b.X, 2) + Math.pow(a.Y - b.Y, 2) + Math.pow(a.Z - b.Z, 2));
            if (mag < Program.CLIP_DISTANCE)
            {
                walls.get(i).Render(Graphics);
            }
        }
        for (int i = 0; i < PickUps.size(); i++)
        {
            Vertex b = PickUps.get(i).Location;
            double mag = Math.sqrt(Math.pow(a.X - b.X, 2) + Math.pow(a.Y - b.Y, 2) + Math.pow(a.Z - b.Z, 2));
            if (mag < Program.CLIP_DISTANCE)
            {
                PickUps.get(i).Render(Graphics);
                PickUpLights.get(i).Render(Graphics);
            }
        }
        for (int i = 0; i < ground.size(); i++)
        {
            Vertex b = ground.get(i).Location;
            double mag = Math.sqrt(Math.pow(a.X - b.X, 2) + Math.pow(a.Y - b.Y, 2) + Math.pow(a.Z - b.Z, 2));
            if (mag < Program.CLIP_DISTANCE)
            {
                ground.get(i).Render(Graphics);
            }
        }
        StartLight.Render(Graphics);
        EndLight.Render(Graphics);
    }
    public Point GetCenterPoint(Point _in)
    {
        return new Point((_in.X * def) - (file.Size.Width * def / 2) + 1.5, (_in.Y * def) - (file.Size.Height * def / 2) + 1.5);
    }
    public Point GetPointFromLocation(Vertex _location)
    {
        return new Point((_location.X - 1.5 + (file.Size.Width * def / 2)) / def, (_location.Z - 1.5 + (file.Size.Height * def / 2)) / def);
    }
    public ArrayList<Rectangle> GetBorders()
    {
        ArrayList<Rectangle> _return = new ArrayList<Rectangle>();
        Size s = new Size(15, 15);
        for (int j = 0; j < file.Size.Height; j++)
        {
            for (int i = 0; i < file.Size.Width; i++)
            {
                String check = file.Get(i, j);
                if (check.equals("B"))
                {
                    _return.add(new Rectangle(Color.White, new Point(i * s.Width, j * s.Height), s));
                }
                else if (check.equals("s"))
                {
                    StartRec = new Rectangle(Color.Red, new Point(i * s.Width, j * s.Height), s);
                }
                else if (check.equals("e"))
                {
                    EndRec = new Rectangle(Color.Green, new Point(i * s.Width, j * s.Height), s);
                }
                else if (check.equals("c"))
                {                    
                    CoinCount++;
                    Object3D obj = new Sphere(15, 2, false, Color.Yellow);
                    Point p = GetCenterPoint(new Point(i, j));
                    obj.Location = new Vertex(p.X, 1, p.Y);
                    obj.Scale = new Vertex(0.25, 0.25, 0.25);
                    obj.Rotation.X = 80;                    
                    PickUps.add(obj);
                    PickUpRects.add(new Rectangle(Color.White, new Point(i * s.Width, j * s.Height), s));
                    PickUpLights.add(new Light(new Vertex(p.X, 0.9, p.Y), new Point(0.10, 0.10), Color.Chocolate));
                }
                else if (check.equals("o"))
                {
                    OpenRects.add(new Rectangle(Color.White, new Point(i, j), s));                    
                }
            }
        }
        return _return;
    }
}