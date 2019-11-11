package unc.cs.checks;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TextBlock;

public class CommentsCheck extends Check {
    public static final String MSG_KEY = "comments";
    @Override
    public int[] getDefaultTokens() {
        return new int[0];
    }

	@Override
    public void beginTree(DetailAST rootAST) {
        final Map<Integer, TextBlock> cppComments = getFileContents()
                .getCppComments();
        final Map<Integer, List<TextBlock>> cComments = getFileContents()
                .getCComments();
        int aCppLength = cppComments.size();
        int aCCommentsLength = cComments.size();
	}

}
