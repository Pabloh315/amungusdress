package com.tuempresa.skinnamungus;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Capas del personaje (superpuestas en el FrameLayout)
    private ImageView ivHat, ivGlasses, ivShirt, ivPants, ivShoes, ivCape;

    // Botones CARGAR
    private Button btnHat, btnGlasses, btnShirt, btnPants, btnShoes, btnCape;

    // Botones QUITAR
    private Button btnRemoveHat, btnRemoveGlasses, btnRemoveShirt, btnRemovePants, btnRemoveShoes, btnRemoveCape;

    // ProgressBars
    private ProgressBar pbHat, pbGlasses, pbShirt, pbPants, pbShoes, pbCape;

    // Reset
    private Button btnReset;

    // Duración de "descarga" (10s)
    private static final int DOWNLOAD_MS = 10_000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias de capas
        ivHat = findViewById(R.id.ivHat);
        ivGlasses = findViewById(R.id.ivGlasses);
        ivShirt = findViewById(R.id.ivShirt);
        ivPants = findViewById(R.id.ivPants);
        ivShoes = findViewById(R.id.ivShoes);
        ivCape = findViewById(R.id.ivCape);

        // Botones CARGAR
        btnHat = findViewById(R.id.btnHat);
        btnGlasses = findViewById(R.id.btnGlasses);
        btnShirt = findViewById(R.id.btnShirt);
        btnPants = findViewById(R.id.btnPants);
        btnShoes = findViewById(R.id.btnShoes);
        btnCape = findViewById(R.id.btnCape);

        // Botones QUITAR
        btnRemoveHat = findViewById(R.id.btnRemoveHat);
        btnRemoveGlasses = findViewById(R.id.btnRemoveGlasses);
        btnRemoveShirt = findViewById(R.id.btnRemoveShirt);
        btnRemovePants = findViewById(R.id.btnRemovePants);
        btnRemoveShoes = findViewById(R.id.btnRemoveShoes);
        btnRemoveCape = findViewById(R.id.btnRemoveCape);

        // ProgressBars
        pbHat = findViewById(R.id.pbHat);
        pbGlasses = findViewById(R.id.pbGlasses);
        pbShirt = findViewById(R.id.pbShirt);
        pbPants = findViewById(R.id.pbPants);
        pbShoes = findViewById(R.id.pbShoes);
        pbCape = findViewById(R.id.pbCape);

        // Reset
        btnReset = findViewById(R.id.btnReset);

        // Listeners CARGAR (ahora pasan también el botón QUITAR)
        btnHat.setOnClickListener(v -> descargarPrenda(btnHat, btnRemoveHat, pbHat, ivHat, "Sombrero"));
        btnGlasses.setOnClickListener(v -> descargarPrenda(btnGlasses, btnRemoveGlasses, pbGlasses, ivGlasses, "Gafas"));
        btnShirt.setOnClickListener(v -> descargarPrenda(btnShirt, btnRemoveShirt, pbShirt, ivShirt, "Camisa"));
        btnPants.setOnClickListener(v -> descargarPrenda(btnPants, btnRemovePants, pbPants, ivPants, "Pantalón"));
        btnShoes.setOnClickListener(v -> descargarPrenda(btnShoes, btnRemoveShoes, pbShoes, ivShoes, "Zapatos"));
        btnCape.setOnClickListener(v -> descargarPrenda(btnCape, btnRemoveCape, pbCape, ivCape, "Capa"));

        // Listeners QUITAR
        btnRemoveHat.setOnClickListener(v -> quitarPrenda(btnHat, btnRemoveHat, pbHat, ivHat, "Sombrero"));
        btnRemoveGlasses.setOnClickListener(v -> quitarPrenda(btnGlasses, btnRemoveGlasses, pbGlasses, ivGlasses, "Gafas"));
        btnRemoveShirt.setOnClickListener(v -> quitarPrenda(btnShirt, btnRemoveShirt, pbShirt, ivShirt, "Camisa"));
        btnRemovePants.setOnClickListener(v -> quitarPrenda(btnPants, btnRemovePants, pbPants, ivPants, "Pantalón"));
        btnRemoveShoes.setOnClickListener(v -> quitarPrenda(btnShoes, btnRemoveShoes, pbShoes, ivShoes, "Zapatos"));
        btnRemoveCape.setOnClickListener(v -> quitarPrenda(btnCape, btnRemoveCape, pbCape, ivCape, "Capa"));

        // Reset (quitar todo)
        btnReset.setOnClickListener(v -> resetPersonaje());
    }

    /**
     * Descarga simulada: muestra ProgressBar, corre Thread 10s.
     * Usa un "estado" con setTag para evitar aplicar si se canceló (quitar/reset) durante la espera.
     */
    private void descargarPrenda(Button btnCargar, Button btnQuitar, ProgressBar pb, ImageView layer, String nombre) {
        // Si ya está aplicada, no repetir
        if (layer.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, nombre + " ya aplicado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Marcar como "corriendo" (para cancelación simple)
        layer.setTag("running");

        btnCargar.setEnabled(false);
        btnQuitar.setEnabled(false);
        pb.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                Thread.sleep(DOWNLOAD_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                // Si mientras tanto se quitó o reseteó, no apliques
                Object tag = layer.getTag();
                if (!(tag instanceof String) || !"running".equals(tag)) {
                    // Asegura UI coherente
                    pb.setVisibility(View.GONE);
                    btnCargar.setEnabled(true);
                    btnQuitar.setEnabled(false);
                    return;
                }

                pb.setVisibility(View.GONE);
                layer.setVisibility(View.VISIBLE);
                layer.setTag("applied"); // nuevo estado

                // Habilita quitar; deshabilita cargar
                btnQuitar.setEnabled(true);
                btnCargar.setEnabled(false);

                Toast.makeText(this, nombre + " aplicado", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    /**
     * Quita una prenda: oculta la capa, cancela si estaba en descarga,
     * oculta ProgressBar y habilita el botón Cargar.
     */
    private void quitarPrenda(Button btnCargar, Button btnQuitar, ProgressBar pb, ImageView layer, String nombre) {
        // Marca como cancelado (cualquier descarga en curso quedará sin aplicar)
        layer.setTag("cancelled");

        // Oculta prenda y ProgressBar
        layer.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.GONE);

        // Restablece transformaciones simples (por si escalaste/trasladaste)
        layer.setScaleX(1f);
        layer.setScaleY(1f);
        layer.setTranslationX(0f);
        layer.setTranslationY(0f);

        // UI botones
        btnCargar.setEnabled(true);
        btnQuitar.setEnabled(false);

        Toast.makeText(this, nombre + " quitado", Toast.LENGTH_SHORT).show();
    }

    /**
     * Quita TODAS las prendas de una vez y restablece la UI.
     */
    private void resetPersonaje() {
        // Sombrero
        quitarPrenda(btnHat, btnRemoveHat, pbHat, ivHat, "Sombrero");
        // Gafas
        quitarPrenda(btnGlasses, btnRemoveGlasses, pbGlasses, ivGlasses, "Gafas");
        // Camisa
        quitarPrenda(btnShirt, btnRemoveShirt, pbShirt, ivShirt, "Camisa");
        // Pantalón
        quitarPrenda(btnPants, btnRemovePants, pbPants, ivPants, "Pantalón");
        // Zapatos
        quitarPrenda(btnShoes, btnRemoveShoes, pbShoes, ivShoes, "Zapatos");
        // Capa
        quitarPrenda(btnCape, btnRemoveCape, pbCape, ivCape, "Capa");

        Toast.makeText(this, "Se reseteó el personaje", Toast.LENGTH_SHORT).show();
    }
}
