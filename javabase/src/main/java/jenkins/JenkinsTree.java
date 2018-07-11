package jenkins;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author muqi.lmq
 * @date 2018/7/5.
 */
public class JenkinsTree<T> {
    private T current;
    private List<JenkinsTree<T>> children;

    public JenkinsTree() {
    }

    public JenkinsTree(T current) {
        this.current = current;
    }

    /**
     * 转换成其他树
     *
     * @param tree
     * @param fun
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> JenkinsTree<R> walkToOtherTree(JenkinsTree<T> tree, Function<T, R> fun, boolean checkAndNotWalk) {
        JenkinsTree<R> other = new JenkinsTree<>();
        if (tree == null) {
            return null;
        } else {
            R apply = fun.apply(tree.getCurrent());
            if (checkAndNotWalk && (apply.getClass().isAssignableFrom(Kv.class) && Kv.class.cast(apply).getV() == null)) {
                return null;
            } else if (apply == null) {
                return null;
            }
            other.setCurrent(apply);
            List<JenkinsTree<T>> children = tree.getChildren();
            if (children != null && children.size() > 0) {
                other.children = children.parallelStream().map(c -> walkToOtherTree(c, fun, checkAndNotWalk))
                        .filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
        return other;
    }

    /**
     * 将tree转成list
     *
     * @param treeJob
     * @param <T>
     * @return
     */
    public static <T> List<T> transToList(JenkinsTree<T> treeJob) {
        List<T> all = new ArrayList<>();
        all.add(treeJob.current);
        List<JenkinsTree<T>> children = treeJob.children;
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                JenkinsTree<T> t_ = children.get(i);
                List<T> ts = transToList(t_);
                if (ts != null && ts.size() > 0) {
                    all.addAll(ts);
                }
            }
        }
        return all;
    }

    public T getCurrent() {
        return current;
    }

    public void setCurrent(T current) {
        this.current = current;
    }

    public List<JenkinsTree<T>> getChildren() {
        return children;
    }

    public void setChildren(List<JenkinsTree<T>> children) {
        this.children = children;
    }
}
