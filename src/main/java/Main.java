import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Graph graph = new Graph();
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入文件路径和文件名：");
            String fileName = scanner.nextLine();
            File file = new File(fileName);
            Scanner scannerFile = new Scanner(file);
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
        }
        Scanner scanner = new Scanner(System.in);
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
                    System.out.println(graph.queryBridgeWords(word1, word2));
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
                    FileWriter writer;
                    try {
                        writer = new FileWriter("./random_walk_output.txt");
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
                                pathText = calcShortestPath(start, nodeText, graph);
                                if (pathText != null) {
                                    System.out.println(start + "到" + nodeText + "最短路径为：" + pathText);
                                }
                            }
                        }
                    } else {
                        pathText = calcShortestPath(start, end, graph);
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
    public static void showDirectedGraph(Graph G, List<Node> nodeList){
            StringBuilder builder = new StringBuilder();
            builder.append("digraph G {\n");
            // 输出所有节点
            for (Node from : G.edges.keySet()) {
                builder.append("    \"").append(from.getText()).append("\"");
                if(nodeList != null && nodeList.contains(from)){
                    // 节点在nodeList中，设置节点颜色为红色
                    builder.append(" [color=\"red\", fontcolor=\"red\"]");
                }
                builder.append(";\n");
            }

            // 输出所有边
            for (Node from : G.edges.keySet()) {
                for (Node to : G.edges.get(from).keySet()) {
                    int weight = G.getEdgeValue(from, to);
                    builder.append("    \"")
                            .append(from.getText())
                            .append("\" -> \"")
                            .append(to.getText())
                            .append("\" [label=\"")
                            .append(weight);
                    if (nodeList != null && nodeList.contains(from) && nodeList.contains(to)
                            && nodeList.indexOf(from) - nodeList.indexOf(to) == -1 ) {
                        builder.append("\", color=\"red\", fontcolor=\"red");
                    }
                    builder.append("\"];\n");
                }
            }
            builder.append("}");
            String outputText = builder.toString();
            FileWriter writer;
            try {
                writer = new FileWriter("./directed-graph.dot");
                writer.write("");       //清空原文件内容
                writer.write(outputText);
                writer.flush();
                writer.close();

                Runtime.getRuntime().exec("dot -Tpng .\\directed-graph.dot -o graph.png"); //使用Graphviz生成图片，需要先安装Graphviz
                Thread.sleep(1500); //等待图片生成

                FileInputStream inStream = new FileInputStream("graph.png");
                BufferedImage bufferedImage = ImageIO.read(inStream);

                int scaledWidth = bufferedImage.getWidth() * 3/ 4;
                int scaledHeight = bufferedImage.getHeight() * 3/ 4;  // 将高度缩小一半
                Image scaledImage = bufferedImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                BufferedImage bufferedScaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);

                bufferedScaledImage.getGraphics().drawImage(scaledImage, 0, 0, null);
                JFrame frame = new JFrame("有向图展示");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                ImageIcon icon = new ImageIcon(bufferedScaledImage);
                JLabel label = new JLabel(icon);
                frame.getContentPane().add(label);
                frame.pack();
                frame.setVisible(true);
            } catch (IOException | InterruptedException e) {
                System.out.println("没有生成图片，请检查Graphviz是否安装正确");
            }
    }
    public static String calcShortestPath(String st, String ed, Graph graph) {
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
            if (adjNodes == null) continue;
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
        pathText += "\n"+ "最短路径长度为：" + distance.get(end);
        return pathText;
    }
}

class Graph {
    Map<Node, HashMap<Node, Integer>> edges;
    Set<String> nodeTexts; // 存储所有节点文本的集合

    public Graph() {
        edges = new HashMap<>();
        nodeTexts = new HashSet<>();

    }

    public void addEdge(Node from, Node to) {
        // 通过文本检查from节点是否存在
        if (!nodeTexts.contains(from.getText())) {
            edges.put(from, new HashMap<>());
            nodeTexts.add(from.getText()); // 添加from节点的文本到集合中
        }else {
            from = getNode(from.getText());
        }
        if(!nodeTexts.contains(to.getText())){
            edges.put(to, new HashMap<>());
            nodeTexts.add(to.getText()); // 添加'to'节点的文本到集合中
        }else{
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
        Node randomToNode = (Node) nodesArray[new Random().nextInt(nodesArray.length)];

        // 创建一个新的HashMap并添加随机选中的边
        HashMap<Node, Integer> edgeMap = new HashMap<>();
        edgeMap.put(randomToNode, adjNodes.get(randomToNode));

        // 将随机边添加到新映射中
        randomEdgeMap.put(from, edgeMap);

        return randomEdgeMap;
    }
    public HashMap<Node, Integer> getEdges(Node from) {
        // 遍历edges映射的键集合来找到具有相同text属性的Node对象
        for (Node node : edges.keySet()) {
            if (node.getText().equals(from.getText())) {
                return edges.get(node); // 返回找到的Node对象的边
            }
        }
        return new HashMap<>(); // 如果没有找到，返回一个空的HashMap
    }

    public Node getNode(String text){
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

    public String getBridgeWord(String word1, String word2) {
        Node from = this.getNode(word1);
        Node to = this.getNode(word2);
        if(from == null || to == null){
            return null;
        }
        // 找到word1的所有邻居节点adjNodes
        HashMap<Node, Integer> adjNodes = this.getEdges(from);
        if (adjNodes == null) {
            return null;
        } else{
            // 遍历word1的所有邻居节点adjNodes
            for (Map.Entry<Node, Integer> adjEntry : adjNodes.entrySet()) {
                Node bridge = adjEntry.getKey();
                HashMap<Node, Integer> bridgeAdjNodes = this.getEdges(bridge);
                // 遍历adjNodes的邻居节点bridgeAdjNodes
                for (Map.Entry<Node, Integer> bridgeAdjEntry : bridgeAdjNodes.entrySet()) {
                    Node bridgeTo = bridgeAdjEntry.getKey();
                    if (bridgeTo.getText().equals(to.getText())) {
                        return bridge.getText();
                    }
                }
            }
        }
        return null;
    }
    public String queryBridgeWords(String word1, String word2) {
        Node from = this.getNode(word1);
        Node to = this.getNode(word2);
        if (from == null && to == null) {
            return "No \""+ word1 +"\" and \""+ word2 +"\" in the graph!";
        }else if(from == null){
            return "No \""+ word1 +"\" in the graph!";
        }else if(to == null){
            return "No \""+ word2 +"\" in the graph!";
        }
        List<String> bridgeWords = new ArrayList<>();
        String bridgeWord = getBridgeWord(word1, word2);
        if(bridgeWord != null){
            bridgeWords.add(bridgeWord);
        }

        if(bridgeWords.size() == 0){
            return "No bridge words from \""+ word1 +"\" to \""+ word2 +"\"!";
        } else if(bridgeWords.size() == 1){
            return "The bridge words from \"" + word1 + "\" to \""+ word2 +"\" is:" + bridgeWords.get(0);
        } else{
            return "The bridge words from \"" + word1 + "\" to \""+ word2 +"\" are:" + bridgeWords.toString();
        }
    }
    public  String generateNewText(String inputText){
        String text = inputText.replaceAll("[\\p{Punct} ]+", " ");  //标点变成空格
        String[] words = text.trim().split("\\s+"); // 使用正则表达式 "\\s+" 来匹配一个或多个空格
        String outputText = words[0].toLowerCase() + " ";
        for (int i = 0; i < words.length - 1; i++) {
            String text1 = words[i].toLowerCase();
            String text2 = words[i+1].toLowerCase();
            String bridgeWord = this.getBridgeWord(text1, text2);
            if(bridgeWord != null){
                outputText += bridgeWord + " " + text2 + " ";
            } else {
                outputText += text2 + " ";
            }
        }
        return outputText;
    }
    public Map<Node, HashMap<Node, Integer>> getRandomEdge() {
        List<Map.Entry<Node, HashMap<Node, Integer>>> edgeList = new ArrayList<>(this.edges.entrySet());
        int randomIndex = new Random().nextInt(edgeList.size());
        Map.Entry<Node, HashMap<Node, Integer>> randomEntry = edgeList.get(randomIndex);

        // 创建一个新的Map，用随机选中的entry填充
        Map<Node, HashMap<Node, Integer>> resultMap = new HashMap<>();
        resultMap.put(randomEntry.getKey(), randomEntry.getValue());

        return resultMap;
    }
    public String randomWalk(){
        Map<Node, HashMap<Node, Integer>> edge = getRandomEdge(); // 随机选择一条边
        HashMap<Node, Integer> to = edge.values().iterator().next();
        Node from = edge.keySet().iterator().next();
        String outputText = from.getText() + " ";
        Scanner scanner = new Scanner(System.in);
        List<Map<Node, HashMap<Node, Integer>>> edgeList = new ArrayList<>(); // 边集合，存放所有已经遍历过的边
        while(edge != null && !edgeList.contains(edge) && to.keySet().iterator().hasNext()){
            edgeList.add(edge);
            edge = this.getRandomEdges(to.keySet().iterator().next());
            if(edge != null){
                from = edge.keySet().iterator().next();
                to = edge.values().iterator().next();
                outputText += from.getText() + " ";
            }
            String inputText = scanner.nextLine();
            if(inputText.equals("q")){
                break;
            }
        }
        if(to.keySet().iterator().hasNext()){
            outputText += to.keySet().iterator().next().getText() + " ";
        }
        return outputText;
    }

}

class Node {
    private String text; // 节点的名称

    // 构造函数
    public Node(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}


