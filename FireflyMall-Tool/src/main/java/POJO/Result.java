package POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private int code;
    private String msg;
    private Object data;

    public static Result success(){
        Result result = new Result();
        result.setCode(HttpStatus.OK.value());
        result.setMsg("响应成功");
        return result;
    }

    public static Result success(HttpStatus status, String msg){
        Result result = new Result();
        result.setCode(status.value());
        result.setMsg(msg);
        return result;
    }

    public static Result success(Object data){
        Result result = success();
        result.setData(data);
        return result;
    }

    public static Result error(){
        Result result = new Result();
        result.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        result.setMsg("响应失败");
        return result;
    }

    public static Result error(HttpStatus status, String msg){
        Result result = new Result();
        result.setCode(status.value());
        result.setMsg(msg);
        return result;
    }

}
