package Model;

import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyModel extends Observable implements IModel {

    private int[][] maze;
    private Maze mazeObject;
    private int[] solution;
    private int currentRowCharPos;
    private int currentColCharPos;
    private Server generatorServer;
    private Server solverServer;
    private int startRow; private int startCol;
    private int endRow; private int endCol;
    private String characterName;


    // Getter for the character's name
    public String getCharacterName() {
        return characterName;
    }

    // Starting the server
    public void startServers()
    {

        generatorServer = new Server(5407, 1000, new ServerStrategyGenerateMaze());
        solverServer = new Server(5408, 1000, new ServerStrategySolveSearchProblem());
        generatorServer.start();
        solverServer.start();

    }



    // Getter for the start row
    public int getStartRow() {
        return startRow;
    }

    // Getter for the start column
    public int getStartCol() {
        return startCol;
    }

    // Getter for the end row
    public int getEndRow() {
        return endRow;
    }

    // Getter for the end column
    public int getEndCol() {
        return endCol;
    }

    // Generating the maze using the rows, columns and character's name
    @Override
    public void generateMaze(int height, int width, String CharacterName) {
        this.characterName = CharacterName;
        try {
            Socket socket = new Socket(InetAddress.getLocalHost(), 5407);
            InputStream inFromServer = socket.getInputStream();
            OutputStream outToServer = socket.getOutputStream();
            ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
            ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
            toServer.flush();
            int[] mazeDimensions = new int[]{height, width};
            toServer.writeObject(mazeDimensions); //send maze dimensions to server
            toServer.flush();
            byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
            InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
            byte[] decompressedMaze = new byte[height*width+12 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
            is.read(decompressedMaze); //Fill decompressedMaze with bytes
            mazeObject = new Maze(decompressedMaze);
            startRow = mazeObject.getStartPosition().getRowIndex();
            startCol = mazeObject.getStartPosition().getColumnIndex();
            endRow = mazeObject.getGoalPosition().getRowIndex();
            endCol = mazeObject.getGoalPosition().getColumnIndex();
            currentRowCharPos = startRow;
            currentColCharPos = startCol;
            this.maze = mazeObject.getGrid();
            socket.close();
        }
        catch (Exception e) {
            Server.LOG.debug("Generate : Communicate Error, Class: MyModel");
        }
        setChanged();
        notifyObservers("maze-new");
    }

    // Getter for the maze
    @Override
    public int[][] getMaze() {
        return maze;
    }

    // Getter for the solution
    @Override
    public int[] getSolution()
    {
        return solution;
    }

    // Moving the character according to the key pressed
    @Override
    public void moveCharacter(KeyCode movement) {
        switch (movement) {
            case UP:
                if(currentRowCharPos-1 >= 0 && maze[currentRowCharPos-1][currentColCharPos]==0)
                {
                    setPosition(-1, 0);
                    setChanged();
                    notifyObservers("movement-up");
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case NUMPAD8:
                if(currentRowCharPos-1 >= 0 && maze[currentRowCharPos-1][currentColCharPos]==0)
                {
                    setPosition(-1, 0);
                    setChanged();
                    notifyObservers("movement-up");
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case DOWN:
                if(currentRowCharPos+1 < maze.length && maze[currentRowCharPos+1][currentColCharPos]==0)
                {
                    setPosition(1, 0);
                    setChanged();
                    notifyObservers("movement-down");
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case NUMPAD2:
                if(currentRowCharPos+1 < maze.length && maze[currentRowCharPos+1][currentColCharPos]==0)
                {
                    setPosition(1, 0);
                    setChanged();
                    notifyObservers("movement-down");
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case RIGHT:
                if(currentColCharPos+1 < maze[0].length && maze[currentRowCharPos][currentColCharPos+1]==0)
                {
                    setPosition(0, 1);
                    setChanged();
                    notifyObservers("movement-right");
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case NUMPAD6:
                if(currentColCharPos+1 < maze[0].length && maze[currentRowCharPos][currentColCharPos+1]==0)
                {
                    setPosition(0, 1);
                    setChanged();
                    notifyObservers("movement-right");
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case LEFT:
                if(currentColCharPos-1 >= 0 && maze[currentRowCharPos][currentColCharPos-1]==0)
                {
                    setPosition(0, -1);
                    setChanged();
                    notifyObservers("movement-left");
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case NUMPAD4:
                if(currentColCharPos-1 >= 0 && maze[currentRowCharPos][currentColCharPos-1]==0)
                {
                    setPosition(0, -1);
                    setChanged();
                    notifyObservers("movement-left");

                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case NUMPAD1://down right
                if(currentRowCharPos+1 < maze.length && currentColCharPos-1 >= 0 && maze[currentRowCharPos+1][currentColCharPos-1]==0) {
                    setPosition(1, -1);
                    if (maze[currentRowCharPos][currentColCharPos-1] == 1)
                    {
                        setChanged();
                        notifyObservers("movement-down");
                    }
                    else
                    {
                        setChanged();
                        notifyObservers("movement-left");
                    }
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case NUMPAD3://down right
                if(currentRowCharPos+1 < maze.length && currentColCharPos+1 < maze[0].length && maze[currentRowCharPos+1][currentColCharPos+1]==0){
                    setPosition(1, 1);
                    if (maze[currentRowCharPos][currentColCharPos+1] == 1)
                    {
                        setChanged();
                        notifyObservers("movement-down");
                    }
                    else
                    {
                        setChanged();
                        notifyObservers("movement-right");
                    }
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case NUMPAD7://up left
                if(currentRowCharPos-1 >= 0 && currentColCharPos-1 >= 0 && maze[currentRowCharPos-1][currentColCharPos-1]==0) {
                    setPosition(-1, -1);
                    if (maze[currentRowCharPos][currentColCharPos-1] == 1)
                    {
                        setChanged();
                        notifyObservers("movement-up");
                    }
                    else
                    {
                        setChanged();
                        notifyObservers("movement-left");
                    }
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case NUMPAD9:// up right
                if(currentRowCharPos-1 >= 0 && currentColCharPos+1 < maze[0].length && maze[currentRowCharPos-1][currentColCharPos+1]==0) {
                    setPosition(-1, 1);
                    if (maze[currentRowCharPos][currentColCharPos+1] == 1)
                    {
                        setChanged();
                        notifyObservers("movement-up");
                    }
                    else
                    {
                        setChanged();
                        notifyObservers("movement-right");
                    }
                }
                else
                {
                    setChanged();
                    notifyObservers("movement-walk-into-wall");
                }
                break;
            case HOME:
                currentRowCharPos = startRow;
                currentColCharPos = startCol;
                setChanged();
                notifyObservers("movement-down");

                break;

        }
    }

    private void setPosition(int rowDelta, int colDelta) {
        currentRowCharPos += rowDelta;
        currentColCharPos += colDelta;

    }

    // Getter for the character's row
    @Override
    public int getCharacterPositionRow() {
        return currentRowCharPos;
    }

    // Getter for the character's column
    @Override
    public int getCharacterPositionColumn() {
        return currentColCharPos;
    }

    // Solving the maze
    @Override
    public void solveMaze() {
        if(mazeObject!=null) {
            try {
                Socket socket = new Socket(InetAddress.getLocalHost(), 5408);
                InputStream inFromServer = socket.getInputStream();
                OutputStream outToServer = socket.getOutputStream();
                ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                toServer.flush();
                toServer.writeObject(mazeObject); //send maze to server
                toServer.flush();
                Solution mazeSolution = (Solution) fromServer.readObject();
                String sol = mazeSolution.toString();
                solution = getSolution(sol);
            }
            catch (Exception e) {
                Server.LOG.debug("Solve - Communicate Error, Class: MyModel");
            }
            setChanged();
            notifyObservers("solution");
        }
    }

    // Getter for the solution from a string
    private int[] getSolution(String sol){
        List<Integer> solList = new LinkedList<>();
        String tmp = "";
        for (int i = 0; i <sol.length() ; i++) {

            if (sol.charAt(i) == '{')
                continue;
            else if (sol.charAt(i) == ',' || sol.charAt(i) == '}')
            {
                solList.add(Integer.parseInt(tmp));
                tmp = "";
            }
            else
                tmp = tmp + sol.charAt(i);
        }
        int[] res = new int[solList.size()];
        for (int i = 0; i < res.length; i++)
            res [i] = solList.get(i);

        return res;
    }

    // Closing the servers
    @Override
    public void close() {
        generatorServer.stop();
        solverServer.stop();
    }

    // Saving a game to a file
    public void saveGame(File fileToSaveTo){
        //FORMAT :: "characterName,charRowPos,charColPos,StartRow,startCol,endRow,endCol,numOfRows,numOfCols,**maze**"
        String game = characterName +",";
        game +=""+currentRowCharPos+","+currentColCharPos+","+startRow+","+startCol+","+endRow+","+endCol+","+maze.length+","+maze[0].length+",";
        for (int i = 0; i <maze.length ; i++) {
            for (int j = 0; j <maze[0].length ; j++) {
                game+= maze[i][j]+",";
            }
        }
        if (fileToSaveTo != null) {
            PrintWriter writer;
            try {
                writer = new PrintWriter(fileToSaveTo);
                writer.println(game);
                writer.close();
            } catch (FileNotFoundException e) {
                Server.LOG.debug("Save - file not found exception");
            }
        }

    }

    // Loading a game from a file loaded
    public void loadGame(File fileToOpen){
        if (fileToOpen != null)
        {
            StringBuilder contentBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(fileToOpen)))
            {
                String sCurrentLine;
                while ((sCurrentLine = br.readLine()) != null)
                {
                    contentBuilder.append(sCurrentLine);
                }
            }
            catch (IOException e)
            {
                Server.LOG.debug("Load - IO Exception");
            }
            String game = contentBuilder.toString();
            String[] parsedString = game.split(",");
            characterName = parsedString[0];
            currentRowCharPos = Integer.parseInt(parsedString[1]);
            currentColCharPos=Integer.parseInt(parsedString[2]);;
            startRow = Integer.parseInt(parsedString[3]);
            startCol = Integer.parseInt(parsedString[4]);
            endRow = Integer.parseInt(parsedString[5]);
            endCol = Integer.parseInt(parsedString[6]);
            int rows = Integer.parseInt(parsedString[7]);
            int cols = Integer.parseInt(parsedString[8]);
            int index = 9;

            maze = new int[rows][cols];
            for (int i = 0; i <rows && index<parsedString.length ; i++) {
                for (int j = 0; j <cols && index<parsedString.length ; j++) {
                    maze[i][j] = Integer.parseInt(parsedString[index]);
                    index++;
                }
            }
            mazeObject = new Maze(maze, startRow, startCol, endRow, endCol);
            setChanged();
            notifyObservers("maze-load");
        }
    }

}
