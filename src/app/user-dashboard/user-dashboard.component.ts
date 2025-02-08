import { Component, OnInit } from '@angular/core';
import { UserControllerService } from '../api';
import { Users } from '../api/model/users';
import { Router } from '@angular/router';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['user-dashboard.component.css'],
  standalone: true,
  imports:[CommonModule,]
})
export class UserDashboardComponent implements OnInit {
  user?: Users;  // Utilisateur spécifique
  loading: boolean = true;  // Indicateur de chargement
  error: string | null = null;  // Message d'erreur

  constructor(private router: Router, private userService: UserControllerService) {}

  ngOnInit(): void {
    this.loadUserDetails();
  }
  loadUserDetails(): void {
    this.userService.getUserDetails().subscribe({
      next: (data) => {
        if (data instanceof Blob) {
          // Convert Blob to JSON
          data.text().then((text) => {
            this.user = JSON.parse(text); // Stocke les données utilisateur
            this.loading = false; // Fin du chargement
          });
        } else {
          this.user = data; // Si déjà en JSON, l'assigne directement
          this.loading = false; // Fin du chargement
        }
      },
      error: (err) => {
        console.error('Error loading user details:', err);
        this.error = 'Failed to load user details'; // Affiche une erreur
        this.loading = false; // Fin du chargement malgré l'erreur
      }
    });
  }

}
