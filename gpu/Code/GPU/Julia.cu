// Inclusion de la bibliothèque pour la sauvegarde d'images
#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"

#include <iostream>
#include <chrono>
#include <cuda_runtime.h>

// Structure pour représenter les nombres complexes
struct cuComplex {
    float r; // Partie réelle
    float i; // Partie imaginaire

    // Constructeur pour initialiser un nombre complexe avec des valeurs données
    __device__ cuComplex(float a, float b) : r(a), i(b) {}

    // Méthode pour calculer le carré de la magnitude d'un nombre complexe
    __device__ float magnitude2(void) { return r * r + i * i; }

    // Surcharge de l'opérateur de multiplication pour les nombres complexes
    __device__ cuComplex operator*(const cuComplex &a) {
        return cuComplex(r * a.r - i * a.i, i * a.r + r * a.i);
    }

    // Surcharge de l'opérateur d'addition pour les nombres complexes
    __device__ cuComplex operator+(const cuComplex &a) {
        return cuComplex(r + a.r, i + a.i);
    }
};

// Kernel CUDA pour calculer la fractale de Julia
__global__ void juliaKernel(unsigned char *ptr, int width, int height) {
    // Calcul des coordonnées du pixel dans l'image
    int x = threadIdx.x + blockIdx.x * blockDim.x;
    int y = threadIdx.y + blockIdx.y * blockDim.y;
    int offset = x + y * width;

    // Définition de la constante de Julia et du facteur d'échelle
    const float scale = 1.5;
    float jx = scale * (float)(width / 2 - x) / (width / 2);
    float jy = scale * (float)(height / 2 - y) / (height / 2);
    cuComplex c(-0.8, 0.156); // Constante de Julia (peut être modifiée pour changer la forme de la fractale)
    cuComplex a(jx, jy); // Nombre complexe correspondant aux coordonnées du pixel

    // Calcul de la fractale de Julia
    int iterations = 0;
    while (iterations < 200 && a.magnitude2() < 1000) {
        a = a * a + c;
        iterations++;
    }

    // Attribution des valeurs de couleurs au pixel
    ptr[offset * 4 + 0] = iterations * iterations % 256; // Rouge
    ptr[offset * 4 + 1] = iterations % 256;               // Vert
    ptr[offset * 4 + 2] = iterations % 256;               // Bleu
    ptr[offset * 4 + 3] = 255;                            // Alpha
}

int main() {
    int DIM; // Résolution de l'image

    // Demande à l'utilisateur la résolution de l'image
    std::cout << "Veuillez saisir la valeur de la résolution de l'image : ";
    std::cin >> DIM;

    // Allocation de mémoire pour l'image sur le GPU
    unsigned char *dev_bitmap;
    unsigned char *bitmap = new unsigned char[4 * DIM * DIM];

    cudaMalloc((void **)&dev_bitmap, sizeof(unsigned char) * 4 * DIM * DIM);

    // Définition de la taille des blocs et des grilles pour le kernel CUDA
    dim3 threadsPerBlock(16, 16);
    dim3 numBlocks(DIM / threadsPerBlock.x, DIM / threadsPerBlock.y);

    // Création d'événements CUDA pour mesurer le temps d'exécution du kernel
    cudaEvent_t start, stop;
    cudaEventCreate(&start);
    cudaEventCreate(&stop);

    cudaEventRecord(start);

    // Appel du kernel CUDA pour calculer la fractale de Julia
    juliaKernel<<<numBlocks, threadsPerBlock>>>(dev_bitmap, DIM, DIM);

    cudaEventRecord(stop);
    cudaEventSynchronize(stop);

    // Calcul du temps d'exécution du kernel
    float milliseconds = 0;
    cudaEventElapsedTime(&milliseconds, start, stop);

    // Affichage du temps d'exécution du kernel
    std::cout << "Temps d'exécution du kernel : " << milliseconds << " ms" << std::endl;

    // Copie des données de l'image du GPU vers le CPU
    cudaMemcpy(bitmap, dev_bitmap, sizeof(unsigned char) * 4 * DIM * DIM, cudaMemcpyDeviceToHost);

    // Libération de la mémoire allouée sur le GPU
    cudaFree(dev_bitmap);

    // Sauvegarde de l'image fractale au format PNG
    auto start_time = std::chrono::high_resolution_clock::now();
    stbi_write_png("julia_cuda.png", DIM, DIM, 4, bitmap, DIM * 4);
    auto end_time = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end_time - start_time).count();

    // Affichage de la confirmation de sauvegarde et du temps total d'exécution du programme
    std::cout << "Image sauvegardée !" << std::endl;
    std::cout << "Temps d'exécution : " << duration << " ms" << std::endl;
    std::cout << "Temps d'exécution en minutes : " << duration / 60000 << " min " << (duration % 60000) / 1000 << " s" << std::endl;

    // Libération de la mémoire allouée pour l'image sur le CPU
    delete[] bitmap;

    return 0;
}

//ESIEA - 4A - Thomas COSSET - Jean-Juc LAURENT - Théo BACHELERY