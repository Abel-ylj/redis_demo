package cn.ylj.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : ylj
 * create at:  2021/2/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String username;
    private int age;
}