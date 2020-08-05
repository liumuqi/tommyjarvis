package argrith.trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @Author:lmq
 * @Date: 2020/7/29
 * @Desc:
 **/
public class TravelTreeNode {

    public static void main(String[] args) {

    }

    /**
     * 前序遍历
     * //    func preorderTraversal(root *TreeNode) []int {
     * //        // 非递归
     * //        if root == nil{
     * //            return nil
     * //        }
     * //        result:=make([]int,0)
     * //        stack:=make([]*TreeNode,0)
     * //
     * //        for root!=nil || len(stack)!=0{
     * //            for root !=nil{
     * //                // 前序遍历，所以先保存结果
     * //                result=append(result,root.Val)
     * //                stack=append(stack,root)
     * //                root=root.Left
     * //            }
     * //            // pop
     * //            node:=stack[len(stack)-1]
     * //            stack=stack[:len(stack)-1]
     * //            root=node.Right
     * //        }
     * //        return result
     * //    }
     *
     * @param treeNode
     *
     * @return
     */
    //通过非递归遍历
    public static List<Object> frontTravel(TreeNode treeNode) {
        List<Object> result = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        while (treeNode != null || stack.size() > 0) {
            while (treeNode != null) {
                result.add(treeNode.val);
                treeNode = treeNode.left;
                stack.push(treeNode);
            }
            TreeNode passedLeft = stack.pop();
            treeNode = passedLeft.right;
        }
        return result;
    }

    //    // 思路：通过stack 保存已经访问的元素，用于原路返回
//    func inorderTraversal(root *TreeNode) []int {
//        result := make([]int, 0)
//        if root == nil {
//            return result
//        }
//        stack := make([]*TreeNode, 0)
//        for len(stack) > 0 || root != nil {
//            for root != nil {
//                stack = append(stack, root)
//                root = root.Left // 一直向左
//            }
//            // 弹出
//            val := stack[len(stack)-1]
//            stack = stack[:len(stack)-1]
//            result = append(result, val.Val)
//            root = val.Right
//        }
//        return result
//    }
//通过非递归遍历
    public static List<Object> midTravel(TreeNode treeNode) {
        List<Object> result = new ArrayList<>();
        return result;
    }
}
