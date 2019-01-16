import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ProfileAddress from './profile-address';
import ProfileAddressDetail from './profile-address-detail';
import ProfileAddressUpdate from './profile-address-update';
import ProfileAddressDeleteDialog from './profile-address-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ProfileAddressUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ProfileAddressUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ProfileAddressDetail} />
      <ErrorBoundaryRoute path={match.url} component={ProfileAddress} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={ProfileAddressDeleteDialog} />
  </>
);

export default Routes;
