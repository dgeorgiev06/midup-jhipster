import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Invitee from './invitee';
import InviteeDetail from './invitee-detail';
import InviteeUpdate from './invitee-update';
import InviteeDeleteDialog from './invitee-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={InviteeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={InviteeUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={InviteeDetail} />
      <ErrorBoundaryRoute path={match.url} component={Invitee} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={InviteeDeleteDialog} />
  </>
);

export default Routes;
