package com.me.simulator.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.me.simulator.R;
import com.me.simulator.data.MatchesAPI;
import com.me.simulator.databinding.ActivityMainBinding;
import com.me.simulator.domain.Match;
import com.me.simulator.ui.adapter.MatchesAdapter;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MatchesAPI matchesApi;
    private MatchesAdapter matchesAdapter = new MatchesAdapter((Collections.emptyList())); //adapter vazio


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupHttpClient();
        setupMatchesList();
        setupMatchesRefresh();
        setupFloatActionButton();
    }

    private void setupHttpClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://felipesjc17.github.io/matches-simulator-api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
      matchesApi = retrofit.create(MatchesAPI.class);
    }

    private void setupMatchesList() {
        //Listar as partidas, consumindo nossa API.

        // melhora performace RecycleView tamanho fixo
        binding.rvMatches.setHasFixedSize(true);

        //setando layout simples aplicando o contexto
        binding.rvMatches.setLayoutManager(new LinearLayoutManager(this));

        //Atribuindo o adapter vazio para rv, para swipe funcionar mesmo antes da API ser carregada
        binding.rvMatches.setAdapter(matchesAdapter);

        findMatchesFromApi();
    }

    private void setupMatchesRefresh() {
        // Atualizar as partidas na ação de swipe.
        binding.srlMatches.setOnRefreshListener(this::findMatchesFromApi);
    }

    private void setupFloatActionButton() {
        //criar evento de click e simulação de partidas e animação do botão.
        binding.fabSimulate.setOnClickListener(view -> {
            view.animate().rotation(360).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // implementar o algoritmo de simulação de partidas.
                    Random random = new Random();
                    for (int i = 0; i < matchesAdapter.getItemCount(); i++){//varrendo partidas
                        Match match = matchesAdapter.getMatches().get(i);//pegando partidas
                        match.getHomeTeam().setScore(random.nextInt(match.getHomeTeam().getStars() + 1));// de 0 até número de estrelas
                        match.getAwayTeam().setScore(random.nextInt(match.getAwayTeam().getStars() + 1));
                        matchesAdapter.notifyItemChanged(i);//notificando RecycleView posição que foi Atualizada
                    }
                }
            });
            throw new RuntimeException("Teste Crashlytics");
        });

    }

    private void findMatchesFromApi() {
        binding.srlMatches.setRefreshing(true);//começo da chamada
        matchesApi.getMatches().enqueue(new Callback<List<Match>>() {
            @Override
            public void onResponse(@NonNull Call<List<Match>> call, @NonNull Response<List<Match>> response) {
                if(response.isSuccessful()) {
                    List<Match> matches = response.body();
                    //instanciando adapter passando lista de partidas carregadas
                    matchesAdapter = new MatchesAdapter(matches);
                    //passando adapter já com a lista carregada e setando em RecycleView
                    binding.rvMatches.setAdapter(matchesAdapter);
                }else{
                    showErrorMessage();
                }
                binding.srlMatches.setRefreshing(false);//terminou a chamada com erro ou não
            }

            @Override
            public void onFailure(@NonNull Call<List<Match>> call,  @NonNull Throwable t) {
                showErrorMessage();
            }
        });
    }

    private void showErrorMessage() {
        Snackbar.make(binding.fabSimulate, R.string.error_api, Snackbar.LENGTH_LONG).show();
    }


}
