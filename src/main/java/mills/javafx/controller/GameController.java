package mills.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GameController {

    @FXML
    private Label testLabel;

    @FXML
    private Shape spot11;

    private Paint dragPaint;
    private Paint previousPaint;



    public void slotClicked(MouseEvent mouseEvent){

        Shape clicked=(Shape) mouseEvent.getTarget();
        clicked.setFill(Color.TRANSPARENT);

    }

    public void dragStarted(MouseEvent mouseEvent){


        Shape clicked=(Shape) mouseEvent.getTarget();
        dragPaint=clicked.getFill();
        Dragboard db = clicked.startDragAndDrop(TransferMode.ANY);
        ClipboardContent cb = new ClipboardContent();
        cb.putString(dragPaint.toString());

        db.setContent(cb);
        mouseEvent.consume();

        System.out.println("Drag started");

    }


    public void dragEntered(DragEvent event){

        if (event.getDragboard().hasString()){
            event.acceptTransferModes(TransferMode.ANY);
        }
        Shape clicked=(Shape) event.getTarget();
        previousPaint= clicked.getFill();
        clicked.setFill(dragPaint);
        System.out.println(event.getDragboard().getString());
        System.out.println("Drag entered");
    }
    public void dragOver(DragEvent event){

        if (event.getDragboard().hasString()){
            event.acceptTransferModes(TransferMode.ANY);
        }


    }

    public void dragDropped(DragEvent event){

        Shape clicked=(Shape) event.getTarget();
        previousPaint=dragPaint;
        clicked.setFill(dragPaint);
        System.out.println("drag dropped");
    }

    public void handleDragDone(DragEvent event){
        Shape clicked=(Shape) event.getTarget();
        clicked.setFill(Color.TRANSPARENT);
    }

    public void dragExited(DragEvent event){

        Shape clicked=(Shape) event.getTarget();
        clicked.setFill(previousPaint);
        //clicked.setFill(dragPaint);
        System.out.println("drag exited");
    }





}
