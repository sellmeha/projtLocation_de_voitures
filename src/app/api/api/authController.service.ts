import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {map, Observable, Subscription, tap} from 'rxjs';
import { AuthRequestDTO } from '../model/authRequestDTO';
import { environment } from '../environments/environment';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthControllerService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {
  }


  public authenticateUser(authRequestDTO: AuthRequestDTO): Observable<any> {
    const headers = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post(`${this.apiUrl}/auth/login`, authRequestDTO, {headers, withCredentials: true});
  }


  public refreshToken(): Observable<any> {
    const headers = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post(`${this.apiUrl}/auth/refresh-token`, null, {headers, withCredentials: true});
  }

  public registerUser(authRequestDTO: AuthRequestDTO): Observable<string> {
    const headers = new HttpHeaders({'Content-Type': 'application/json'});
    return this.http.post('http://localhost:8080/auth/register', authRequestDTO, {headers, responseType: 'text'});
  }

  private userRole: { role: string } | null = null;


  getUserRole(): Observable<string> {
    return this.http.get<{ role: string }>('http://localhost:8080/api/user/role', {withCredentials: true}).pipe(
      map(http => http.role)
    );
  }


  hasRole(expectedRole: string): Observable<boolean> {
    return new Observable(observer => {
      this.getUserRole().subscribe(role => {
        observer.next(role === expectedRole);
      }, error => {
        observer.next(false);
      });
    });
  }



  logout(): Observable<void> {
    return this.http.post<void>('http://localhost:8080/auth/logout', {}, {withCredentials: true }).pipe(
      tap(() => {
        document.cookie = 'access_token=; Max-Age=0; path=/';
        document.cookie = 'refresh_token=; Max-Age=0; path=/';
      })
    );
  }
}

  @Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const cloned = req.clone({
      withCredentials: true     });
    return next.handle(cloned);
  }
}

