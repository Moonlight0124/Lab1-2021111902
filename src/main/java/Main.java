import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.apache.commons.io.FilenameUtils;

/**
 * 主类，包含程序的入口和主要功能.
 */
public class Main {
  /**
   * 程序的入口.
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    Graph graph = new Graph();
    try {
      Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
      System.out.println("请输入文件路径和文件名：");
      String fileName = scanner.nextLine();
      File file = new File(FilenameUtils.getName(fileName));
      Scanner scannerFile = new Scanner(file, StandardCharsets.UTF_8);
      scannerFile.useDelimiter("[^A-Za-z]+"); // 使用正则表达式作为分隔符
      String firstText = scannerFile.next().toLowerCase();
      String secondText;
      while (scannerFile.hasNext()) {
        secondText = scannerFile.next().toLowerCase();
        graph.addEdge(new Node(firstText), new Node(secondText));
        firstText = secondText;
      }
      scannerFile.close();
    } catch (FileNotFoundException e) {
      System.out.println("找不到文件！");
      e.printStackTrace();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    while (true) {
      System.out.println("=======================================");
      System.out.println("请选择功能：");
      System.out.println("1.显示有向图");
      System.out.println("2.查询桥接词");
      System.out.println("3.根据桥接词生成新文本");
      System.out.println("4.随机游走算法");
      System.out.println("5.最短路径算法");
      System.out.println("6.退出");
      System.out.println("请输入功能序号：");
      String command = scanner.nextLine();
      switch (command) {
        case "1" -> showDirectedGraph(graph, null);
        case "2" -> {
          System.out.println("================查询桥接词================");
          System.out.println("请输入两个word，以空格分隔：");
          String word1 = scanner.next();
          String word2 = scanner.next();
          System.out.println(queryBridgeWords(graph, word1, word2));
          scanner.nextLine();               // 清除缓冲区中的换行符
        }
        case "3" -> {
          System.out.println("===============根据桥接词生成文本===============");
          System.out.println("请输入一段文本：");
          String inputText = scanner.nextLine();
          System.out.println("生成的新文本：");
          System.out.println(graph.generateNewText(inputText));
        }
        case "4" -> {
          System.out.println("===============随机游走算法================");
          System.out.println("输入q退出随机游走...");
          System.out.println("输入其他字符继续随机游走...");
          String outputText = graph.randomWalk();
          System.out.println("随机游走算法生成的新文本：");
          System.out.println(outputText);
          try {
            Writer writer = new OutputStreamWriter(new FileOutputStream("./random_walk_output.txt"),
                StandardCharsets.UTF_8);
            writer.write("");   //清空原文件内容
            writer.write(outputText);
            writer.flush();
            writer.close();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
        case "5" -> {
          System.out.println("================最短路径算法================");
          String pathText;
          System.out.print("请输入起始词：");
          String start = scanner.nextLine();
          System.out.print("请输入终止词：");
          String end = scanner.nextLine();
          //如果end为空，输出start到其他所有结点的最短路径
          if (end.isEmpty()) {
            for (String nodeText : graph.nodeTexts) {
              if (!nodeText.equals(start)) {
                pathText = calcShortestPath(graph, start, nodeText);
                if (pathText != null) {
                  System.out.println(start + "到" + nodeText + "最短路径为：" + pathText);
                }
              }
            }
          } else {
            pathText = calcShortestPath(graph, start, end);
            if (pathText != null) {
              System.out.println(start + "到" + end + "最短路径是：" + pathText);
            } else {
              System.out.println(start + "和" + end + "不可达！");
            }
          }
        }
        case "6" -> {
          System.out.println("=================退出程序==================");
          System.exit(0);
        }
        default -> System.out.println("输入错误，请重新输入！");
      }
    }
  }

  /**
   * 显示有向图.
   *
   * @param graph    有向图对象
   * @param nodeList 节点列表，用于标记特殊节点
   */
  public static void showDirectedGraph(Graph graph, List<Node> nodeList) {
    StringBuilder builder = new StringBuilder();
    builder.append("digraph G {\n");
    // 输出所有节点
    for (Node from : graph.edges.keySet()) {
      builder.append("    \"").append(from.getText()).append("\"");
      if (nodeList != null && nodeList.contains(from)) {
        // 节点在nodeList中，设置节点颜色为红色
        builder.append(" [color=\"red\", fontcolor=\"red\"]");
      }
      builder.append(";\n");
    }

    // 输出所有边
    for (Map.Entry<Node, HashMap<Node, Integer>> entry : graph.edges.entrySet()) {
      Node from = entry.getKey();
      for (Map.Entry<Node, Integer> innerEntry : entry.getValue().entrySet()) {
        Node to = innerEntry.getKey();
        int weight = innerEntry.getValue();
        builder.append("    \"")
            .append(from.getText())
            .append("\" -> \"")
            .append(to.getText())
            .append("\" [label=\"")
            .append(weight);
        if (nodeList != null && nodeList.contains(from) && nodeList.contains(to)
            && nodeList.indexOf(from) - nodeList.indexOf(to) == -1) {
          builder.append("\", color=\"red\", fontcolor=\"red");
        }
        builder.append("\"];\n");
      }
    }
    builder.append("}");
    String outputText = builder.toString();
    try {
      Writer writer = new OutputStreamWriter(new FileOutputStream("./directed-graph.dot"),
          StandardCharsets.UTF_8);
      writer.write("");       //清空原文件内容
      writer.write(outputText);
      writer.flush();
      writer.close();

      Runtime.getRuntime().exec("dot -Tpng .\\directed-graph.dot"
          + " -o graph.png"); //使用Graphviz生成图片，需要先安装Graphviz
      Thread.sleep(1500); //等待图片生成

      FileInputStream inStream = new FileInputStream("graph.png");
      byte[] graphImg = new byte[inStream.available()];
      inStream.read(graphImg);
      inStream.close();

      JFrame frame = new JFrame("有向图展示");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      ImageIcon icon = new ImageIcon(graphImg);
      JLabel label = new JLabel(icon);
      frame.getContentPane().add(label);
      frame.pack();
      frame.setVisible(true);

    } catch (IOException | InterruptedException e) {
      System.out.println("没有生成图片，请检查Graphviz是否安装正确");
    }
  }

  /**
   * 计算最短路径.
   *
   * @param st    起始词
   * @param ed    终止词
   * @param graph 有向图对象
   * @return 最短路径的字符串表示，若不可达则返回null
   */
  public static String calcShortestPath(Graph graph, String st, String ed) {
    if (graph == null) {
      System.out.println("图为空");
      return null;
    }
    Node start = graph.getNode(st);
    Node end = graph.getNode(ed);
    if (start == null || end == null) {
      System.out.println("起始词或终止词不存在");
      return null;
    }
    if (start.equals(end)) {
      System.out.println("起始词和终止词相同");
      return null;
    }
    // 记录到达每个节点的最短距离
    Map<Node, Integer> distance = new HashMap<>();
    // 每个节点的前驱节点
    Map<Node, Node> predecessor = new HashMap<>();
    // 初始化
    for (Node node : graph.edges.keySet()) {
      distance.put(node, Integer.MAX_VALUE);
      predecessor.put(node, null);
    }
    distance.put(start, 0);

    Set<Node> visited = new HashSet<>();  // 用于记录已处理的节点

    // 开始算法
    while (true) {
      Node minNode = null;
      int minDistance = Integer.MAX_VALUE;
      // 选择距离当前点最近的点
      for (Map.Entry<Node, Integer> entry : distance.entrySet()) {
        if (!visited.contains(entry.getKey()) && entry.getValue() < minDistance) {
          minNode = entry.getKey();
          minDistance = entry.getValue();
        }
      }
      // 如果没有找到最小距离的节点，跳出循环
      if (minNode == null || minDistance == Integer.MAX_VALUE) {
        break;
      }
      visited.add(minNode);  // 将当前节点标记为已处理

      // 更新距离
      Map<Node, Integer> adjNodes = graph.edges.get(minNode);
      if (adjNodes == null) {
        continue;
      }
      for (Map.Entry<Node, Integer> adjEntry : adjNodes.entrySet()) {
        Node to = adjEntry.getKey();
        int weight = adjEntry.getValue();
        int newDistance = distance.get(minNode) + weight;
        if (newDistance < distance.get(to)) {
          distance.put(to, newDistance);
          predecessor.put(to, minNode);
        }
      }
    }
    // 回溯路径
    List<Node> path = new ArrayList<>();
    Node current = end;
    while (current != null) {
      path.add(0, current);
      current = predecessor.get(current);
    }
    String pathText = "";
    //遍历输出path的节点
    if (!path.isEmpty()) {
      int size = path.size();
      for (int i = 0; i < size; i++) {
        Node node = path.get(i);
        pathText += node.getText();
        if (i < size - 1) {
          pathText += "->";
        }
      }
    }
    showDirectedGraph(graph, path);
    pathText += "\n" + "最短路径长度为：" + distance.get(end);
    if (distance.get(end) == Integer.MAX_VALUE) {
      return null;
    }
    return pathText;
  }

  /**
   * 查询桥接词.
   *
   * @param word1    起始词
   * @param word2    终止词
   * @param graph    有向图对象
   * @return 查询结果字符串
   */
  public static String queryBridgeWords(Graph graph, String word1, String word2) {
    Node from = graph.getNode(word1);
    Node to = graph.getNode(word2);
    if (from == null && to == null) {
      return "No \"" + word1 + "\" and \"" + word2 + "\" in the graph!";
    } else if (from == null) {
      return "No \"" + word1 + "\" in the graph!";
    } else if (to == null) {
      return "No \"" + word2 + "\" in the graph!";
    }
    // 找到word1的所有邻居节点adjNodes
    HashMap<Node, Integer> adjNodes = graph.getEdges(from);
    List<String> bridgeWords = new ArrayList<>();
    if (adjNodes == null) {
      return null;
    } else {
      // 遍历word1的所有邻居节点adjNodes
      for (Map.Entry<Node, Integer> adjEntry : adjNodes.entrySet()) {
        Node bridge = adjEntry.getKey();
        HashMap<Node, Integer> bridgeAdjNodes = graph.getEdges(bridge);
        // 遍历adjNodes的邻居节点bridgeAdjNodes
        for (Map.Entry<Node, Integer> bridgeAdjEntry : bridgeAdjNodes.entrySet()) {
          Node bridgeTo = bridgeAdjEntry.getKey();
          if (bridgeTo.getText().equals(to.getText())) {
            bridgeWords.add(bridge.getText());
          }
        }
      }
    }

    if (bridgeWords.size() == 0) {
      return "No bridge words from \"" + word1 + "\" to \"" + word2 + "\"!";
    } else if (bridgeWords.size() == 1) {
      return "The bridge words from \"" + word1 + "\" to \""
          + word2 + "\" is:" + bridgeWords.get(0);
    } else {
      return "The bridge words from \"" + word1 + "\" to \""
          + word2 + "\" are:" + bridgeWords.toString();
    }
  }

}
