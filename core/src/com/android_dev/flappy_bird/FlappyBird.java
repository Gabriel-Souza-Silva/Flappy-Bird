package com.android_dev.flappy_bird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ParticleControllerInfluencer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Font;
import java.awt.Rectangle;
import java.util.Random;

import javax.lang.model.type.NullType;

public class FlappyBird extends ApplicationAdapter {

	private SpriteBatch batch;

	private Texture[] passaros;
	private Texture fundo;
	private Texture canoBaixo;
	private Texture canoTopo;
	private Texture gameOver;
	private Random numeroRandomico;
	private Random NumeroRandomicoDois;
	private BitmapFont fonte;
	private BitmapFont TapToStart;
	private BitmapFont mensagem;
	private Circle passaroCirculo;
	private com.badlogic.gdx.math.Rectangle retanguloCanoTopo;
	private com.badlogic.gdx.math.Rectangle retanguloCanoBaixo;
    private com.badlogic.gdx.math.Rectangle retanguloCanoTopoDois;
    private com.badlogic.gdx.math.Rectangle retanguloCanoBaixoDois;
    private ShapeRenderer shape;

	//Atrinbutos
	private float larguraDispositivo;
	private float alturaDispositivo;
	private int estadoJogo = 0; //jogo nao iniciado 1-> iniciado
    private int pontuação = 0;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float  VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

	private float velocidadeQueda = 0;
	private float variacao = 0;
	private float posicaoInicialVertical;
	private float posicaoMovimentoCanoHorizontal;
	private float posicaoMovimentoCanoHorizontalDois;
 	private float espacoEntreCanos = 300;
	private float deltaTime;
	private float alturaEntreCanosRandomica;
	private float alturaEntreCanosRandomicaDois;
	private boolean marcouPonto = false;



	@Override
	public void create () {
        passaroCirculo = new Circle();
        /*shape = new ShapeRenderer();
        retanguloCanoTopo = new com.badlogic.gdx.math.Rectangle();
        retanguloCanoBaixo = new com.badlogic.gdx.math.Rectangle();
        retanguloCanoTopoDois = new com.badlogic.gdx.math.Rectangle();
        retanguloCanoBaixoDois = new com.badlogic.gdx.math.Rectangle();*/


	    numeroRandomico = new Random();
        NumeroRandomicoDois = new Random();
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);
        TapToStart = new BitmapFont();
        TapToStart.setColor(Color.YELLOW);
        TapToStart.getData().setScale(4);
        mensagem = new BitmapFont();
        mensagem.setColor(Color.YELLOW);
        mensagem.getData().setScale(3);

		batch = new SpriteBatch();

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

		fundo = new Texture("fundo.png");
		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		gameOver = new Texture("game_over.png");


        /*CONFIGURANDO A CAMERA*/
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH /2 ,VIRTUAL_HEIGHT/2,0);
		viewport = new StretchViewport(VIRTUAL_WIDTH,VIRTUAL_HEIGHT,camera);

		larguraDispositivo = VIRTUAL_WIDTH;
		alturaDispositivo = VIRTUAL_HEIGHT;

		posicaoInicialVertical = alturaDispositivo/2;

		posicaoMovimentoCanoHorizontal = larguraDispositivo;
        posicaoMovimentoCanoHorizontalDois = posicaoMovimentoCanoHorizontal + 400;
	}

	@Override
	public void render () {
	    camera.update();

	    //limpar frames
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        deltaTime = Gdx.graphics.getDeltaTime();
        variacao += deltaTime * 8;
        if (variacao > 2) variacao = 0;


        //FAZ O JOGO COMEÇAR COM O CLICK;
        if (estadoJogo == 0) {

            if (Gdx.input.justTouched()) {
                estadoJogo = 1;
            }

        } else {

            velocidadeQueda++;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0) {
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;
            }

            if(estadoJogo ==1){

                posicaoMovimentoCanoHorizontal -= deltaTime * 170;
                posicaoMovimentoCanoHorizontalDois -= deltaTime * 170;
                if (Gdx.input.isTouched()) {
                    velocidadeQueda = -12;
                }

                //Verifica se o cano saiu da tela
                if (posicaoMovimentoCanoHorizontal < -canoBaixo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEntreCanosRandomica = numeroRandomico.nextInt(550) - 275;
                    marcouPonto = false;
                }

                if (posicaoMovimentoCanoHorizontalDois < -canoBaixo.getWidth()) {
                    posicaoMovimentoCanoHorizontalDois = larguraDispositivo;
                    alturaEntreCanosRandomicaDois = NumeroRandomicoDois.nextInt(550) - 275;
                    marcouPonto = false;
                }

                //Verifica Pontuação
                if(posicaoMovimentoCanoHorizontal < 120 || posicaoMovimentoCanoHorizontalDois < 120 ){
                    if(!marcouPonto){
                        pontuação += 1;
                        marcouPonto = true;
                    }
                }

            }else{
                //Tela game over
                if(Gdx.input.justTouched()){
                    estadoJogo = 0;
                    pontuação = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo /2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    posicaoMovimentoCanoHorizontalDois = posicaoMovimentoCanoHorizontal + 400;

                }
            }

        }
            //CONFIGURANDO A CAMERA
            batch.setProjectionMatrix(camera.combined);

            batch.begin();
            //fundo
            batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
            //canos
            batch.draw(canoTopo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica);
            batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica);

            batch.draw(canoTopo, posicaoMovimentoCanoHorizontalDois, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomicaDois);
            batch.draw(canoBaixo, posicaoMovimentoCanoHorizontalDois, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomicaDois);
            //passaro
            batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
            fonte.draw(batch,String.valueOf(pontuação),larguraDispositivo/2,alturaDispositivo - 80);


            if(estadoJogo == 0){
                TapToStart.draw(batch,"Tap to Start",200,alturaDispositivo/2 - 300);
            }

            if(estadoJogo == 2){
                batch.draw(gameOver,larguraDispositivo/2 - gameOver.getWidth()/2,alturaDispositivo/2 );
                mensagem.draw(batch,"       Nao Desista \n Toque para Reiniciar!",larguraDispositivo/2 - 200 ,alturaDispositivo/2 - gameOver.getHeight()/2);
            }

            batch.end();

            passaroCirculo.set(120 + passaros[0].getWidth()/2 ,posicaoInicialVertical + passaros[0].getHeight()/2 ,passaros[0].getWidth()/2);
            retanguloCanoBaixo = new com.badlogic.gdx.math.Rectangle(posicaoMovimentoCanoHorizontal,alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomica,canoBaixo.getWidth(),canoBaixo.getHeight());
            retanguloCanoBaixoDois = new com.badlogic.gdx.math.Rectangle(posicaoMovimentoCanoHorizontalDois,alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEntreCanosRandomicaDois,canoBaixo.getWidth(),canoBaixo.getHeight());
            retanguloCanoTopo = new com.badlogic.gdx.math.Rectangle(posicaoMovimentoCanoHorizontal,alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomica,canoTopo.getWidth(),canoTopo.getHeight());
            retanguloCanoTopoDois = new com.badlogic.gdx.math.Rectangle(posicaoMovimentoCanoHorizontalDois,alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEntreCanosRandomicaDois,canoTopo.getWidth(),canoTopo.getHeight());


            //desenhar forma
            /*shape.begin(ShapeRenderer.ShapeType.Filled);
            shape.circle(passaroCirculo.x,passaroCirculo.y,passaroCirculo.radius);
            shape.rect(retanguloCanoBaixo.x,retanguloCanoBaixo.y,retanguloCanoBaixo.width,retanguloCanoBaixo.height);
            shape.rect(retanguloCanoBaixoDois.x,retanguloCanoBaixoDois.y,retanguloCanoBaixoDois.width,retanguloCanoBaixoDois.height);
            shape.rect(retanguloCanoTopo.x,retanguloCanoTopo.y,retanguloCanoTopo.width,retanguloCanoTopo.height);
            shape.rect(retanguloCanoTopoDois.x,retanguloCanoTopoDois.y,retanguloCanoTopoDois.width,retanguloCanoTopoDois.height);
            shape.setColor(Color.RED);
            shape.end();*/

            //Teste de colisao

            if(Intersector.overlaps(passaroCirculo,retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo,retanguloCanoTopo) || Intersector.overlaps(passaroCirculo,retanguloCanoBaixoDois) || Intersector.overlaps(passaroCirculo,retanguloCanoTopoDois)|| posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo){
                estadoJogo = 2;
            }



    }

    @Override
    public void resize(int width, int height){
	    viewport.update(width,height);
    }

}
