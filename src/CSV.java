import studios.vanish.engine.Size;
public class CSV
{
    private String[][] Data;
    public Size Size = new Size();
    public CSV(String file)
    {
        Initialize(file);
    }
    public void Initialize(String file)
    {
        try
        {
            String[] arr = FileUtility.readAsStringArray(file);
            parse(arr);
        }
        catch (Exception e)
        {
            
        }
    }
    private void parse(String[] arr)
    {
        Data = new String[arr.length][0];
        for (int i = 0; i < arr.length; i++)
        {
            Data[arr.length - i - 1] = arr[i].split(",");
        }
        Size = new Size(Data[1].length, Data.length);
    }
    public String Get(int x, int y)
    {
        try
        {
            return Data[y][x];
        }
        catch (Exception e)
        {
            return "err";
        }
    }
    public void Print()
    {
        for (int i = 0; i < Data.length; i++)
        {
            for (int j = 0; j < Data[i].length; j++)
            {
                System.out.print(Data[i][j]);
                if (j < Data[i].length - 1)
                {
                    System.out.println(", ");
                }
            }
            System.out.println();
        }
    }
}
