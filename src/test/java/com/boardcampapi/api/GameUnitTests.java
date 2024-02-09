package com.boardcampapi.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcampapi.api.dtos.GameDTO;
import com.boardcampapi.api.exceptions.GameNameConflictException;
import com.boardcampapi.api.models.GameModel;
import com.boardcampapi.api.repositories.GameRepository;
import com.boardcampapi.api.services.GameService;

@SpringBootTest
class GameUnitTests {
    
    @InjectMocks
    private GameService gameService;

    @Mock
    private GameRepository gameRepository;

    @Test
    void givenRepeatedGame_whenCreatingGame_thenThrowsError() {
        //given
        GameDTO game = new GameDTO("Game", "http://", 1L, 1L);

        doReturn(true).when(gameRepository).existsByName(any());

        //when
        GameNameConflictException exception = assertThrows(
            GameNameConflictException.class,
            () -> gameService.save(game)
        );

        //then
        verify(gameRepository, times(1)).existsByName(any());
        verify(gameRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("A game with this name already exists!", exception.getMessage());
    }

    @Test
    void givenValidGame_whenCreatingGame_thenCreatesGame() {
        //given
        GameDTO game = new GameDTO("Game", "http://", 1L, 1L);
        GameModel newGame = new GameModel(game);

        doReturn(false).when(gameRepository).existsByName(any());
        doReturn(newGame).when(gameRepository).save(any());

        //when
        GameModel result = gameService.save(game);

        //then
        verify(gameRepository, times(1)).existsByName(any());
        verify(gameRepository, times(1)).save(any());
        assertEquals(newGame, result);
    }

}
