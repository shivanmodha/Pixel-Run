import studios.vanish.engine.Object3D;
import studios.vanish.engine.Sphere;
import studios.vanish.engine.Vertex;
import studios.vanish.engine.Index;
import studios.vanish.engine.Color;
import studios.vanish.engine.GraphicsUnit;
public class Person extends Object3D
{
	public Object3D Face;
	public Object3D Eye_Left;
	public Object3D Eye_Right;
	public Object3D Body;
	public Object3D Arm_Left;
	public Object3D Arm_Right;
	public Object3D Leg_Left;
	public Object3D Leg_Right;
	public Object3D Gun;
	public Vertex HitBox = new Vertex(0.5, 0.5, 0.5);
	private Vertex walkAnimation = new Vertex();
	private int walkAnimationDir = 0;
	private Color face;
	public int Difficulty = 0;
	public boolean shoot = false;
	
	public Person()
	{
		face = Program.COLOR_HUMAN_FACE;
		Initialize();
	}
	public void Initialize()
	{
		Face = new Sphere(Program.HUMAN_DETAIL_FACE, Program.HUMAN_DETAIL_FACE, false, face);		
		Eye_Left = new Sphere(Program.HUMAN_DETAIL_EYE, Program.HUMAN_DETAIL_EYE, false, Program.COLOR_HUMAN_EYE);		
		Eye_Right = new Sphere(Program.HUMAN_DETAIL_EYE, Program.HUMAN_DETAIL_EYE, false, Program.COLOR_HUMAN_EYE);		
		Body = new Sphere(Program.HUMAN_DETAIL_BODY, Program.HUMAN_DETAIL_BODY, true, Program.COLOR_HUMAN_BODY);
		Arm_Left = new Sphere(Program.HUMAN_DETAIL_BODY, Program.HUMAN_DETAIL_BODY, true, Program.COLOR_HUMAN_BODY);
		Arm_Right = new Sphere(Program.HUMAN_DETAIL_BODY, Program.HUMAN_DETAIL_BODY, true, Program.COLOR_HUMAN_BODY);
		Leg_Left = new Sphere(Program.HUMAN_DETAIL_BODY, Program.HUMAN_DETAIL_BODY, true, Program.COLOR_HUMAN_BODY);		
		Leg_Right = new Sphere(Program.HUMAN_DETAIL_BODY, Program.HUMAN_DETAIL_BODY, true, Program.COLOR_HUMAN_BODY);
		Gun = new Object3D();
		Gun.Vertices = new Vertex[]
		{
			new Vertex(-0.75, +0.75, -3.00), new Vertex(+0.75, +0.75, -3.00), new Vertex(+0.75, -0.75, -3.00), new Vertex(-0.75, -0.75, -3.00),
			new Vertex(-0.75, +0.75, +3.00), new Vertex(+0.75, +0.75, +3.00), new Vertex(+0.75, -0.75, +3.00), new Vertex(-0.75, -0.75, +3.00),
			new Vertex(-0.75, -4.00, -2.75), new Vertex(+0.75, -4.00, -2.75), new Vertex(+0.75, -4.00, -3.75), new Vertex(-0.75, -4.00, -3.75),
			new Vertex(-0.75, -0.75, -2.00), new Vertex(+0.75, -0.75, -2.00), new Vertex(+0.75, -0.75, -3.00), new Vertex(-0.75, -0.75, -3.00),
		};
		Gun.Indices = new Index[]
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
		Gun.Colors = new Color[]
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
	}
	public void Transform()
	{
		Vertex rotation = Rotation.subtract(new Vertex(0, 0, 90));
		Vertex location = Location.add(new Vertex(0, 0.45, 0));
		Face.Scale = new Vertex(0.35, 0.35, 0.35);
		Face.Location = location.subtract(new Vertex(0, 0.35, 0));
		Face.RevolutionRadius = new Vertex(0, 1, 0);
		Face.Revolution = rotation;
		
		Eye_Left.Scale = new Vertex(0.025, 0.025, 0.025);
		Eye_Left.Location = location;
		Eye_Left.RevolutionRadius = new Vertex(0.5, 0.7, -0.125);
		Eye_Left.Revolution = rotation;
		
		Eye_Right.Scale = new Vertex(0.025, 0.025, 0.025);
		Eye_Right.Location = location;
		Eye_Right.RevolutionRadius = new Vertex(0.5, 0.7, 0.125);
		Eye_Right.Revolution = rotation;
		
		Body.Scale = new Vertex(0.025, 0.4, 0.025);
		Body.Location = location;
		Body.Revolution = rotation;
		
		Arm_Left.Scale = new Vertex(0.025, 0.4, 0.025);
		Arm_Left.Location = location;
		Arm_Left.RevolutionRadius = new Vertex(0, -0.1, 0.10);
		Arm_Left.Revolution = rotation;
		Arm_Left.Rotation = new Vertex(-10, 0, 0);

		Arm_Right.Scale = new Vertex(0.025, 0.4, 0.025);
		Arm_Right.Location = location;
		Arm_Right.RevolutionRadius = new Vertex(0, -0.1, -0.10);
		Arm_Right.Revolution = rotation;
		Arm_Right.Rotation = new Vertex(10, 0, 0);

		Leg_Left.Scale = new Vertex(0.025, 0.4, 0.025);
		Leg_Left.Location = location;
		Leg_Left.RevolutionRadius = new Vertex(0, -0.67, 0.06);
		Leg_Left.Revolution = rotation.add(walkAnimation);
		Leg_Left.Rotation = new Vertex(-10, 0, 0);
		
		Leg_Right.Scale = new Vertex(0.025, 0.4, 0.025);
		Leg_Right.Location = location;
		Leg_Right.RevolutionRadius = new Vertex(0, -0.67, -0.06);
		Leg_Right.Revolution = rotation.add(walkAnimation);
		Leg_Right.Rotation = new Vertex(10, 0, 0);
		
		Gun.Scale = new Vertex(0.05, 0.05, 0.05);
		Gun.Location = location;
		Gun.RevolutionRadius = new Vertex(0.70, 0.5, -0.9);
		Gun.Revolution = rotation;
		Gun.Rotation = new Vertex(0, 0, 90);
	}
	public void Walk(double factor)
	{
		if (walkAnimationDir == 0)
		{
			walkAnimation.Z += factor * 100;
			if (walkAnimation.Z > 45)
			{
				walkAnimationDir = 1;
			}
		}
		else
		{
			walkAnimation.Z -= factor * 100;
			if (walkAnimation.Z < -45)
			{
				walkAnimationDir = 0;
			}
		}
	}
	public void Turn(double factor, Vertex Camera)
	{
		double zAngle = Location.Z - Camera.Z;
		double xAngle = Location.X - Camera.X;
		Rotation.Z = (180 * Math.atan(xAngle / zAngle) / Math.PI);
		if (zAngle > 0)
		{
			Rotation.Z += 180;
		}
	}
	public Vertex GetLocation()
	{
		Vertex _return = new Vertex(Location);
		_return.Y += Gun.RevolutionRadius.Y + .4;
		return _return;
	}
	public void Render(GraphicsUnit Graphics)
	{
		Transform();
		Face.Render(Graphics);
		Eye_Left.Render(Graphics);
		Eye_Right.Render(Graphics);
		Body.Render(Graphics);
		Arm_Left.Render(Graphics);
		Arm_Right.Render(Graphics);
		Leg_Left.Render(Graphics);
		Leg_Right.Render(Graphics);
	}
}
