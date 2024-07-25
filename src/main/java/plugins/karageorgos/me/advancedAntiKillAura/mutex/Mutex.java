package plugins.karageorgos.me.advancedAntiKillAura.mutex;

public class Mutex {

    private boolean status;

    public Mutex(){
        status = false;
    }

    public void lock(){
        status = true;
    }

    public void unlock(){
        status = false;
    }

    public boolean isLocked(){
        return status;
    }

}
