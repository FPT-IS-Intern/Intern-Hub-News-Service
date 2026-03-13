# 07 - API Flows

## Create news
1. Client calls `POST /news`
2. `NewsController` validates payload
3. `NewsUsecase.create` builds model and timestamps
4. Repository saves entity and returns created item

## List/get news
- `GET /news` returns all news
- `GET /news/{id}` returns one item or not-found error

## Update/delete news
- `PUT /news/{id}` updates title/body and `updatedAt`
- `DELETE /news/{id}` removes item after existence check

