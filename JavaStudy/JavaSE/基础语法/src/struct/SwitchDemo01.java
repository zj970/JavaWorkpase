package struct;

public class SwitchDemo01 {
    public static void main(String[] args) {

        char grade = 'C';

        //case穿透
        switch(grade){
            case 'A':
                System.out.println("优秀");
                break;
            case 'B':
                System.out.println("良好");
                break;
            case 'C':
                System.out.println("及格");
            case 'D':
                System.out.println("差点");
            case 'E':
                System.out.println("挂科");
            default:
                System.out.println("未知等级");
        }
    }
}
