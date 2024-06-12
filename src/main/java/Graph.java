import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;


class Graph {
  Map<Node, HashMap<Node, Integer>> edges;
  Set<String> nodeTexts; // 存储所有节点文本的集合
  SecureRandom secureRandom = new SecureRandom();

  public Graph() {
    edges = new HashMap<>();
    nodeTexts = new HashSet<>();

  }

  public void addEdge(Node from, Node to) {
    // 通过文本检查from节点是否存在
    if (!nodeTexts.contains(from.getText())) {
      edges.put(from, new HashMap<>());
      nodeTexts.add(from.getText()); // 添加from节点的文本到集合中
    } else {
      from = getNode(from.getText());
    }
    if (!nodeTexts.contains(to.getText())) {
      edges.put(to, new HashMap<>());
      nodeTexts.add(to.getText()); // 添加'to'节点的文本到集合中
    } else {
      to = getNode(to.getText());
    }
    // 增加边的权值
    edges.get(from).merge(to, 1, Integer::sum);
  }

  public Map<Node, HashMap<Node, Integer>> getRandomEdges(Node from) {
    HashMap<Node, Integer> adjNodes = this.getEdges(from);
    if (adjNodes == null || adjNodes.isEmpty()) {
      return null; // 如果没有邻接节点，返回null
    }
    // 创建一个新的映射来存储随机边
    Map<Node, HashMap<Node, Integer>> randomEdgeMap = new HashMap<>();

    // 获取邻接节点的数组
    Object[] nodesArray = adjNodes.keySet().toArray();

    // 随机选择一个邻接节点
    Node randomToNode = (Node) nodesArray[secureRandom.nextInt(nodesArray.length)];

    // 创建一个新的HashMap并添加随机选中的边
    HashMap<Node, Integer> edgeMap = new HashMap<>();
    edgeMap.put(randomToNode, adjNodes.get(randomToNode));

    // 将随机边添加到新映射中
    randomEdgeMap.put(from, edgeMap);

    return randomEdgeMap;
  }

  public HashMap<Node, Integer> getEdges(Node from) {
    // 遍历edges映射的键集合来找到具有相同text属性的Node对象
    for (Map.Entry<Node, HashMap<Node, Integer>> entry : edges.entrySet()) {
      Node node = entry.getKey();
      if (node.getText().equals(from.getText())) {
        return edges.get(node); // 返回找到的Node对象的边
      }
    }
    return new HashMap<>(); // 如果没有找到，返回一个空的HashMap
  }

  public Node getNode(String text) {
    for (Node node : edges.keySet()) {
      if (node.getText().equals(text)) {
        return node;
      }
    }
    return null;
  }

  public int getEdgeValue(Node from, Node to) {
    return edges.getOrDefault(from, new HashMap<>()).getOrDefault(to, 0);
  }

  public List<String> getBridgeWord(String word1, String word2) {
    Node from = this.getNode(word1);
    Node to = this.getNode(word2);
    if (from == null || to == null) {
      return null;
    }
    // 找到word1的所有邻居节点adjNodes
    HashMap<Node, Integer> adjNodes = this.getEdges(from);
    List<String> bridgeWords = new ArrayList<>();
    if (adjNodes == null) {
      return null;
    } else {
      // 遍历word1的所有邻居节点adjNodes
      for (Map.Entry<Node, Integer> adjEntry : adjNodes.entrySet()) {
        Node bridge = adjEntry.getKey();
        HashMap<Node, Integer> bridgeAdjNodes = this.getEdges(bridge);
        // 遍历adjNodes的邻居节点bridgeAdjNodes
        for (Map.Entry<Node, Integer> bridgeAdjEntry : bridgeAdjNodes.entrySet()) {
          Node bridgeTo = bridgeAdjEntry.getKey();
          if (bridgeTo.getText().equals(to.getText())) {
            bridgeWords.add(bridge.getText());
          }
        }
      }
    }
    return bridgeWords;
  }


  public String generateNewText(String inputText) {
    StringBuilder outputTextBuilder = new StringBuilder();
    String text = inputText.replaceAll("[\\p{Punct} ]+", " ");  //标点变成空格
    String[] words = text.trim().split("\\s+"); // 使用正则表达式 "\\s+" 来匹配一个或多个空格
    outputTextBuilder.append(words[0].toLowerCase()).append(" ");
    for (int i = 0; i < words.length - 1; i++) {
      String text1 = words[i].toLowerCase();
      String text2 = words[i + 1].toLowerCase();
      List<String> bridgeWords = this.getBridgeWord(text1, text2);
      if (bridgeWords != null && !bridgeWords.isEmpty()) {
        String bridgeWord = bridgeWords.get(secureRandom.nextInt(bridgeWords.size()));
        outputTextBuilder.append(bridgeWord).append(" ").append(text2).append(" ");
      } else {
        outputTextBuilder.append(text2).append(" ");
      }
    }
    String outputText = outputTextBuilder.toString();
    return outputText;
  }

  public Map<Node, HashMap<Node, Integer>> getRandomEdge() {
    List<Map.Entry<Node, HashMap<Node, Integer>>> edgeList = new ArrayList<>(this.edges.entrySet());
    int randomIndex = secureRandom.nextInt(edgeList.size());
    Map.Entry<Node, HashMap<Node, Integer>> randomEntry = edgeList.get(randomIndex);

    // 创建一个新的Map，用随机选中的entry填充
    Map<Node, HashMap<Node, Integer>> resultMap = new HashMap<>();
    resultMap.put(randomEntry.getKey(), randomEntry.getValue());

    return resultMap;
  }

  public String randomWalk() {
    StringBuilder outputTextBuilder = new StringBuilder();
    ;
    Map<Node, HashMap<Node, Integer>> edge = getRandomEdge(); // 随机选择一条边
    HashMap<Node, Integer> to = edge.values().iterator().next();
    Node from = edge.keySet().iterator().next();
    outputTextBuilder.append(from.getText()).append(" ");
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    List<Map<Node, HashMap<Node, Integer>>> edgeList = new ArrayList<>(); // 边集合，存放所有已经遍历过的边
    while (edge != null && !edgeList.contains(edge) && to.keySet().iterator().hasNext()) {
      edgeList.add(edge);
      edge = this.getRandomEdges(to.keySet().iterator().next());
      if (edge != null) {
        from = edge.keySet().iterator().next();
        to = edge.values().iterator().next();
        outputTextBuilder.append(from.getText()).append(" ");
      }
      String inputText = scanner.nextLine();
      if (inputText.equals("q")) {
        break;
      }
    }
    if (to.keySet().iterator().hasNext()) {
      outputTextBuilder.append(to.keySet().iterator().next().getText()).append(" ");
    }
    String outputText = outputTextBuilder.toString();
    return outputText;
  }

}
