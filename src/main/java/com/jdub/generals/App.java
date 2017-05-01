package com.jdub.generals;

import pl.joegreen.sergeants.framework.Games;
import pl.joegreen.sergeants.framework.queue.QueueConfiguration;
import pl.joegreen.sergeants.framework.user.UserConfiguration;

/**
 * Created by jameswarren on 4/13/17.
 */
public class App {
    public static void main(String[] args){
//            Games.play(1, Bot1::new,
//                    QueueConfiguration.customGame(true, "jdub_custom_game"),
//                    UserConfiguration.onlyUserId("jdub_bot")).forEach(System.out::println);

        Games.play(1, Bot1::new,
                QueueConfiguration.freeForAll(false),
                UserConfiguration.idAndName("jdub_bot1", "com.jdub.bot1")).forEach(System.out::println);

    }
}
