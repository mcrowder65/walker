package generic;
import java.util.Comparator;

public class FScoreComparator implements Comparator<NodeIndexWithValue>
{
    @Override
    public int compare(NodeIndexWithValue x, NodeIndexWithValue y)
    {
        // Assume neither string is null. Real code should
        // probably be more robust
        // You could also just return x.length() - y.length(),
        // which would be more efficient.
        if (x.fScoreValue < y.fScoreValue)
        {
            return -1;
        }
        if (x.fScoreValue > y.fScoreValue)
        {
            return 1;
        }
        return 0;
    }
}