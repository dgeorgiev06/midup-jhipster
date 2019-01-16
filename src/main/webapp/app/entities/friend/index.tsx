import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Friend from './friend';
import FriendDetail from './friend-detail';
import FriendUpdate from './friend-update';
import FriendDeleteDialog from './friend-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={FriendUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={FriendUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={FriendDetail} />
      <ErrorBoundaryRoute path={match.url} component={Friend} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={FriendDeleteDialog} />
  </>
);

export default Routes;
