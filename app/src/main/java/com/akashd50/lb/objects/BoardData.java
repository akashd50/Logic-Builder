package com.akashd50.lb.objects;


public class BoardData{
    public LogicObject logicObject;
    public int x, y;
    public BoardData(LogicObject l, int x, int y){
        this.logicObject = l;this.x=x;this.y=y;
    }
    public String toString(){
        return "{"+ logicObject + ", " + x + ", " + y +"}";
    }
}